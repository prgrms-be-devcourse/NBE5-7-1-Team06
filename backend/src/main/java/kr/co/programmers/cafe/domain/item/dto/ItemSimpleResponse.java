package kr.co.programmers.cafe.domain.item.dto;


import kr.co.programmers.cafe.domain.item.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemSimpleResponse {
    private Long id;
    private String name;
    private Integer price;
    private Category category;
    private String imageName;
}
