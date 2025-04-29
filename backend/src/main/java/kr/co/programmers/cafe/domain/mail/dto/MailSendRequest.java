package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public abstract class MailSendRequest {
    private String mailAddress;
    private String address;
    private String zipCode;
    private Integer totalPrice;
    private LocalDateTime sendTime;
    private List<ItemMailSendRequest> items;
}
