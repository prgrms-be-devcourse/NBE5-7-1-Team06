package kr.co.programmers.cafe.domain.order.dto;

import kr.co.programmers.cafe.domain.order.entity.Status;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class OrderStatusChangeRequest {
    private Status status;
}
