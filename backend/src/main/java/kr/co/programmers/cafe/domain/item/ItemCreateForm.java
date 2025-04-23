package kr.co.programmers.cafe.domain.item;

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
