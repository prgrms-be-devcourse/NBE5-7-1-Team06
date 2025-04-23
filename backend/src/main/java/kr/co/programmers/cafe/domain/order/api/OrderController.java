package kr.co.programmers.cafe.domain.order.api;

import kr.co.programmers.cafe.domain.order.app.OrderService;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> order(@RequestBody OrderRequest orderRequest) {
        Long orderId = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(orderId);
    }

}
