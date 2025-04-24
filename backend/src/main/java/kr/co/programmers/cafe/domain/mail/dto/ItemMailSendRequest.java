package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemMailSendRequest {
    private String name;
    private Integer quantity;
    private Integer price;
}