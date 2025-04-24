package kr.co.programmers.cafe.domain.item.app;

import kr.co.programmers.cafe.domain.item.dto.ItemCreateForm;
import kr.co.programmers.cafe.domain.item.dto.ItemEditForm;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
import kr.co.programmers.cafe.domain.order.entity.Category;
import kr.co.programmers.cafe.domain.order.entity.Item;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import kr.co.programmers.cafe.global.util.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FileManager fileManager;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private ItemCreateForm createForm;
    private ItemEditForm editForm;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .name("Americano")
                .price(4500)
                .category(Category.A)
                .image("test-image.jpg")
                .build();

        mockFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        createForm = new ItemCreateForm();
        createForm.setName("Americano");
        createForm.setPrice(4500);
        createForm.setCategory("A");
        createForm.setImage(mockFile);

        editForm = new ItemEditForm();
        editForm.setId(1L);
        editForm.setName("Latte");
        editForm.setPrice(5000);
        editForm.setCategory("B");
        editForm.setImage(mockFile);
    }

    @Test
    void createItem_Success() {
        given(fileManager.saveFile(any())).willReturn("saved-image.jpg");
        given(itemRepository.save(any(Item.class))).willReturn(testItem);

        Long itemId = itemService.create(createForm);

        assertThat(itemId).isEqualTo(1L);
        verify(fileManager).saveFile(any());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void deleteItem_Success() {
        given(itemRepository.findById(1L)).willReturn(Optional.of(testItem));

        itemService.delete(1L);

        verify(itemRepository).delete(testItem);
        verify(fileManager).deleteFile(testItem.getImage());
    }

    @Test
    void deleteItem_NotFound() {
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.delete(1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void findAllItems_Success() {
        given(itemRepository.findAll()).willReturn(List.of(testItem));

        List<ItemResponse> items = itemService.findAll();

        assertThat(items).hasSize(1);
        assertThat(items.get(0)).usingRecursiveComparison().isEqualTo(ItemResponse.of(testItem));
    }

    @Test
    void findItemById_Success() {
        given(itemRepository.findById(1L)).willReturn(Optional.of(testItem));

        ItemResponse response = itemService.findById(1L);

        assertThat(response).usingRecursiveComparison().isEqualTo(ItemResponse.of(testItem));
    }

    @Test
    void findItemById_NotFound() {
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById(1L))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void editItem_Success() throws IOException {
        given(itemRepository.findById(1L)).willReturn(Optional.of(testItem));
        given(fileManager.saveFile(any())).willReturn("new-image.jpg");
        given(itemRepository.save(any(Item.class))).willReturn(testItem);

        ItemResponse response = itemService.edit(editForm);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(editForm.getId());
        assertThat(response.getName()).isEqualTo(editForm.getName());
        assertThat(response.getPrice()).isEqualTo(editForm.getPrice());
        assertThat(response.getCategory()).isEqualTo(Category.valueOf(editForm.getCategory()));
        assertThat(response.getImageName()).isEqualTo("new-image.jpg");
        verify(fileManager).deleteFile(any());
        verify(fileManager).saveFile(any());
    }

    @Test
    void editItem_NotFound() {
        given(itemRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.edit(editForm))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void findAllItems_WithPaging_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> itemPage = new PageImpl<>(List.of(testItem), pageable, 1);
        given(itemRepository.findAll(pageable)).willReturn(itemPage);

        Page<ItemResponse> result = itemService.findAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).usingRecursiveComparison()
                .isEqualTo(ItemResponse.of(testItem));
        verify(itemRepository).findAll(pageable);
    }

}