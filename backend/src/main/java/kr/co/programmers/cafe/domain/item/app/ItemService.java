package kr.co.programmers.cafe.domain.item.app;

import jakarta.transaction.Transactional;
import kr.co.programmers.cafe.domain.item.dao.ItemRepository;
import kr.co.programmers.cafe.domain.item.dto.ItemCreateForm;
import kr.co.programmers.cafe.domain.item.dto.ItemEditForm;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
import kr.co.programmers.cafe.domain.item.dto.ItemSimpleResponse;
import kr.co.programmers.cafe.domain.item.entity.Category;
import kr.co.programmers.cafe.domain.item.entity.Item;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import kr.co.programmers.cafe.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileManager fileManager;

    /**
     * ItemCreateForm 기반으로 Item을 만들고 저장한다,
     * 첨부된 이미지도 저장한다.
     *
     * @param itemCreateForm
     * @return 생성된 Item의 id값
     */
    public Long create(ItemCreateForm itemCreateForm) {
        String filePath = fileManager.saveFile(itemCreateForm.getImage());
        Item item = Item.builder()
                .name(itemCreateForm.getName())
                .price(itemCreateForm.getPrice())
                .category(Category.valueOf(itemCreateForm.getCategory()))
                .image(filePath)
                .build();

        return itemRepository.save(item).getId();
    }

    /**
     * 제공된 아이템 ID를 기반으로 아이템을 삭제합니다. 아이템을 찾을 수 없는 경우
     * {@link ItemNotFoundException}을 발생시킵니다. 아이템과 연결된
     * 이미지 파일도 함께 삭제됩니다.
     *
     * @param id 삭제할 아이템의 ID
     * @throws ItemNotFoundException 주어진 ID의 아이템을 찾을 수 없는 경우
     */
    public void delete(Long id) throws ItemNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new ItemNotFoundException(id));
        itemRepository.delete(item);
        fileManager.deleteFile(item.getImage());
    }

    public List<ItemResponse> findAll() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::of)
                .toList();
    }

    /**
     * 생성, 수정 시간을 제외한 정보만 전달해주도록 findAll 기능추가
     */
    public List<ItemSimpleResponse> findAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(item -> ItemSimpleResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .category(item.getCategory())
                        .imageName(item.getImage())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 페이징을 지원하는 전체 아이템 조회
     *
     * @param pageable 페이징 정보
     * @return 페이징된 ItemResponse 목록
     */
    public Page<ItemResponse> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(ItemResponse::of);
    }


    public ItemResponse findById(Long id) throws ItemNotFoundException {
        return ItemResponse.of(itemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException(id)
        ));
    }


    /**
     * 제공된 정보로 기존 아이템을 업데이트합니다. 이미지가 제공된 경우,
     * 기존 이미지를 삭제하고 새로운 이미지로 교체합니다. 아이템이 존재하지 않는 경우
     * 예외가 발생합니다.
     *
     * @param itemEditForm 이름, 가격, 카테고리 및 선택적 새 이미지 파일을 포함한
     *                     아이템의 업데이트된 상세 정보가 담긴 폼
     * @return 수정된 아이템 상세 정보가 포함된 {@code ItemResponse}
     */
    public ItemResponse edit(ItemEditForm itemEditForm) throws ItemNotFoundException {
        Item item = itemRepository.findById(itemEditForm.getId()).orElseThrow(
                () -> new ItemNotFoundException(itemEditForm.getId())
        );
        String filePath = null;
        if (!itemEditForm.getImage().isEmpty()) {
            fileManager.deleteFile(item.getImage());
            filePath = fileManager.saveFile(itemEditForm.getImage());
        }
        item.update(itemEditForm.getName(),
                itemEditForm.getDescription(),
                itemEditForm.getPrice(),
                Category.valueOf(itemEditForm.getCategory()),
                filePath);
        return ItemResponse.of(itemRepository.save(item));
    }

    public Resource getImage(Long itemId) {
        String filePath = findById(itemId).getImageName();
        Resource imageResource = fileManager.getFileResource(filePath);
        return imageResource;
    }

}
