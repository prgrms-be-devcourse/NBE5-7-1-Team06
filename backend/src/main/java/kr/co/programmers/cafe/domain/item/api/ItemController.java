package kr.co.programmers.cafe.domain.item.api;

import kr.co.programmers.cafe.domain.item.app.ItemService;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
import kr.co.programmers.cafe.global.util.FileManager;
import kr.co.programmers.cafe.domain.item.dto.ItemSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final FileManager fileManager;

    /**
     * 페이지네이션이 적용된 아이템 목록을 조회합니다.
     *
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 페이지당 아이템 수 (기본값: 10)
     * @return 페이지네이션이 적용된 아이템 목록
     */
    @GetMapping
    public ResponseEntity<Page<ItemResponse>> readItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemResponse> itemResponses = itemService.findAll(pageable);
        return ResponseEntity.ok(itemResponses);
    }

    /**
     * 특정 아이템의 상세 정보를 조회합니다.
     *
     * @param itemId 조회할 아이템의 ID
     * @return 아이템 상세 정보
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> readItem(@PathVariable Long itemId) {
        ItemResponse itemResponse = itemService.findById(itemId);
        return ResponseEntity.ok(itemResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemSimpleResponse>> readAllItems(){
        List<ItemSimpleResponse> items = itemService.findAllItems();
        return ResponseEntity.ok(items);
    }

    /**
     * 특정 아이템의 이미지를 조회합니다.
     * 이미지 파일 확장자에 따라 적절한 MediaType을 설정합니다.
     * 지원하는 이미지 형식: JPG, JPEG, GIF, PNG
     *
     * @param itemId 이미지를 조회할 아이템의 ID
     * @return 이미지 리소스
     */
    @GetMapping("/{itemId}/images")
    public ResponseEntity<Resource> readItemImages(@PathVariable Long itemId) {
        Resource resource = itemService.getImage(itemId);
        String filePath = resource.getFilename();

        MediaType mediaType = fileManager.getMediaType(filePath);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }


}
