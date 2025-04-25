package kr.co.programmers.cafe.domain.item.dto;

import kr.co.programmers.cafe.domain.item.entity.Category;
import kr.co.programmers.cafe.domain.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Item}
 */
@AllArgsConstructor
@Data
@Builder
public class ItemResponse implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final Integer price;
    private final Category category;
    private final String imageName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .category(item.getCategory())
                .imageName(item.getImage())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}