package kr.co.programmers.cafe.domain.item.api;

import kr.co.programmers.cafe.domain.item.app.ItemService;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
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

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> readItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemResponse> itemResponses = itemService.findAll(pageable);
        return ResponseEntity.ok(itemResponses);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponse> readItem(@PathVariable Long itemId) {
        ItemResponse itemResponse = itemService.findById(itemId);
        return ResponseEntity.ok(itemResponse);
    }

    @GetMapping("/{itemId}/images")
    public ResponseEntity<Resource> readItemImages(@PathVariable Long itemId) {
        Resource resource = itemService.getImage(itemId);
        String filePath = resource.getFilename();

        MediaType mediaType = MediaType.IMAGE_PNG;
        if (filePath != null) {
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            switch (extension) {
                case "jpg":
                case "jpeg":
                    mediaType = MediaType.IMAGE_JPEG;
                    break;
                case "gif":
                    mediaType = MediaType.IMAGE_GIF;
                    break;
                case "png":
                    mediaType = MediaType.IMAGE_PNG;
                    break;
            }
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
