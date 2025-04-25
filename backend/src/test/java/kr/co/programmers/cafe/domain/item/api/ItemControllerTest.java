package kr.co.programmers.cafe.domain.item.api;

import kr.co.programmers.cafe.domain.item.app.ItemService;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
import kr.co.programmers.cafe.domain.item.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@WithMockUser
class ItemControllerTest {

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;
    private ItemResponse testItemResponse;

    @BeforeEach
    void setUp() {
        testItemResponse = ItemResponse.builder()
                .id(1L)
                .name("Americano")
                .price(4500)
                .category(Category.A)
                .imageName("test-image.jpg")
                .build();
    }

    @Test
    void getAllItems_Success() throws Exception {
        // 변경 가능한 리스트 사용
        Page<ItemResponse> page = new PageImpl<>(List.of(testItemResponse));

        given(itemService.findAll(any())).willReturn(page);

        mockMvc.perform(get("/api/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(testItemResponse.getId()))
                .andExpect(jsonPath("$.content[0].name").value(testItemResponse.getName()));
    }

    @Test
    void getItemById_Success() throws Exception {
        given(itemService.findById(1L)).willReturn(testItemResponse);

        mockMvc.perform(get("/api/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItemResponse.getId()))
                .andExpect(jsonPath("$.name").value(testItemResponse.getName()));
    }

    @Test
    void getImage_Success() throws Exception {
        byte[] dummyImageData = "test-image.jpg".getBytes();
        Resource mockResource = new ByteArrayResource(dummyImageData) {
            @Override
            public String getFilename() {
                return "test-image.jpg";
            }
        };

        given(itemService.getImage(anyLong())).willReturn(mockResource);

        mockMvc.perform(get("/api/items/{id}/images", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }
}