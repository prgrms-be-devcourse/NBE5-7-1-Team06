package kr.co.programmers.cafe.domain.order.dto;

import kr.co.programmers.cafe.domain.order.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String email;
    private String address;
    private String zipCode;
    private List<OrderItemResponse> orderItems;
    private Integer totalPrice;
    private Status status;
    private LocalDateTime orderedAt;
}