package kr.co.programmers.cafe.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String email;

    private String address;

    private String zipCode;

    private List<OrderItemRequest> orderItemRequests;

    private Integer totalPrice;

}
