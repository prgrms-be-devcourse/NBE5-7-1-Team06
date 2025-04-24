package kr.co.programmers.cafe.domain.admin;

import kr.co.programmers.cafe.domain.order.app.OrderService;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.dto.OrderStatusChangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    //    private final AdminService adminService;
    private final OrderService orderService;

    // 로그인 실패의 경우 error 를 로그인 페이지로 넘겨주기 위한 메서드
    @GetMapping("/login")
    public String adminLogin(@RequestParam(required = false) String error, Model model) {
        log.info("로그인 페이지");
        if(error != null) {
            switch (error) {
                case "ip_blocked" -> model.addAttribute("errorMessage", "외부 IP 에선 접속이 불가능합니다!");
                case "login-failed" -> model.addAttribute("errorMessage", "올바르지 않은 계정입니다!");
            }
            log.error("로그인 실패");
        }
        return "admin/login-form";
    }

    // 로그인 성공 테스트 용
    @GetMapping("/manage")
    public String adminManage() {
        log.info("관리 페이지 호출 테스트 - 로그인 성공");
        return "admin/manage-form";
    }

    //관리자 - 주문 조회 메서드
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    //관리자 - 주문 상태 변환 메서드
    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<Void> changeOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusChangeRequest request
    ) {
        orderService.changeOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok().build();
    }

}
