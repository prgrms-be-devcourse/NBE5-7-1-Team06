package kr.co.programmers.cafe.items.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class ItemCreateForm {
    private String name;
    private String category;
    private int price;
    private MultipartFile image;
    
}
