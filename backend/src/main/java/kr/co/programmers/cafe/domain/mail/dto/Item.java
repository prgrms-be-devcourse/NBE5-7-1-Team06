package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 상품 Entity, DTO 작업 전 임시 DTO
 */
@Deprecated
@Getter
public class Item {
    private String name;
    private int quantity;
    private int price;

    @Builder
    public Item(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
