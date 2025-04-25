package kr.co.programmers.cafe.domain.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class ItemEditForm {
    private Long id;
    private String name;
    private String description;
    private String category;
    private int price;
    private MultipartFile image;
    private String imageUrl;

}
