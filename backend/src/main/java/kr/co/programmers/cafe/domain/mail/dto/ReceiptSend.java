package kr.co.programmers.cafe.domain.mail.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 주문 Entity, DTO 작업 전 임시 DTO
 */
@Deprecated
@Getter
public class ReceiptSend {
    private String targetAddress;
    private String subject;
    private String orderId;
    private String orderedAt;
    private String address;
    private String zipCode;
    private int totalPrice;
    private List<Item> items;

    @Builder
    public ReceiptSend(
            String targetAddress, String subject, String orderId, String orderedAt, String address,
            String zipCode, List<Item> items, int totalPrice) {
        this.targetAddress = targetAddress;
        this.subject = subject;
        this.orderId = orderId;
        this.orderedAt = orderedAt;
        this.address = address;
        this.zipCode = zipCode;
        this.items = items;
        this.totalPrice = totalPrice;
    }
}
