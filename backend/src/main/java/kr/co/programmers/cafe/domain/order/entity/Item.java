package kr.co.programmers.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    private String image;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public Item(String name, Integer price, Category category, String image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
    }

    public void update(String name, Integer price, Category category, String image) {
        if(name != null) {
            this.name = name;
        }
        if(price != null) {
            this.price = price;
        }
        if(category != null) {
            this.category = category;
        }
        if(image != null) {
            this.image = image;
        }
    }
}
