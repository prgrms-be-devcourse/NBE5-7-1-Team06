package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DeliveringMailSendRequest {
    private String mailAddress;
    private String address;
    private String zipCode;
    private Integer totalPrice;
    private LocalDateTime deliveryStartedAt;
    private List<ItemMailSendRequest> items;
}
