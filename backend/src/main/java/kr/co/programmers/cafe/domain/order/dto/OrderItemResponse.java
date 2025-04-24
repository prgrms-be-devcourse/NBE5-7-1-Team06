package kr.co.programmers.cafe.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    private String name;
    private Integer price;
    private Integer quantity;
}