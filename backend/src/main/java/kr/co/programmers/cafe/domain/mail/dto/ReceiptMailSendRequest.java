package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReceiptMailSendRequest {
    private String mailAddress;
    private Long orderId;
    private String address;
    private String zipCode;
    private Integer totalPrice;
    private LocalDateTime orderedAt;
    private List<ItemMailSendRequest> items;
}
