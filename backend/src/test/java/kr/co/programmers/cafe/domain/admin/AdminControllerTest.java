package kr.co.programmers.cafe.domain.admin;

import kr.co.programmers.cafe.domain.item.ItemCreateForm;
import kr.co.programmers.cafe.domain.item.ItemEditForm;
import kr.co.programmers.cafe.domain.item.ItemResponse;
import kr.co.programmers.cafe.domain.item.ItemService;
import kr.co.programmers.cafe.domain.order.entity.Category;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import kr.co.programmers.cafe.global.util.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private FileManager fileManager;

    private ItemResponse testItem;

    @BeforeEach
    void setUp() {
        testItem = ItemResponse.builder()
                .id(1L)
                .name("아메리카노")
                .price(4500)
                .category(Category.A)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItemForm_Success() throws Exception {
        mockMvc.perform(get("/admin/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/items/item-new"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditForm_Success() throws Exception {
        given(itemService.findById(1L)).willReturn(testItem);

        mockMvc.perform(get("/admin/items/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/items/item-edit"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getItems_Success() throws Exception {
        List<ItemResponse> items = List.of(testItem);
        Page<ItemResponse> page = new PageImpl<>(items);
        given(itemService.findAll(any())).willReturn(page);

        mockMvc.perform(get("/admin/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/items/item-list"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        given(itemService.create(any(ItemCreateForm.class))).willReturn(1L);


        mockMvc.perform(multipart("/admin/items")
                        .file(imageFile)
                        .param("name", "아메리카노")
                        .param("price", "4500")
                        .param("category", "A").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/items"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_Success() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        given(itemService.edit(any(ItemEditForm.class))).willReturn(testItem);

        MockHttpServletRequestBuilder multipartRequest = multipart("/admin/items/edit/1")
                .file(imageFile)
                .param("id", "1")
                .param("name", "라떼")
                .param("price", "5000")
                .param("category", "A").with(csrf());

        multipartRequest.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        mockMvc.perform(multipartRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/items"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEditForm_WithInvalidId_ShouldReturn404() throws Exception {
        given(itemService.findById(999L)).willThrow(new ItemNotFoundException(999L));

        mockMvc.perform(get("/admin/items/edit/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_WithoutCsrf_ShouldReturn403() throws Exception {
        mockMvc.perform(multipart("/admin/items/1")
                        .with(request -> {
                            request.setMethod("DELETE");
                            return request;
                        }))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_WithInvalidId_ShouldReturn404() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        given(itemService.edit(any(ItemEditForm.class))).willThrow(new ItemNotFoundException(1L));

        MockHttpServletRequestBuilder multipartRequest = multipart("/admin/items/edit/1")
                .file(imageFile)
                .param("id", "1")
                .param("name", "라떼")
                .param("price", "5000")
                .param("category", "A")
                .with(csrf());

        multipartRequest.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        mockMvc.perform(multipartRequest)
                .andExpect(status().isNotFound());
    }

}