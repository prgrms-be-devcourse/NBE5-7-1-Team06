package kr.co.programmers.cafe.domain.admin;

import kr.co.programmers.cafe.domain.order.app.OrderService;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.dto.OrderStatusChangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import kr.co.programmers.cafe.domain.order.entity.Status;
import java.util.List;
import java.util.Optional;

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


    @GetMapping("/items")
    public String items(Model model) {
        /*Item item = Item.builder().name("상품1").price(1000).id(1L).category(Item.Category.Coffee).imageUrl("temp.png").build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        model.addAttribute("items", items);*/
        return "items/item-list";
    }

    @GetMapping
    public String main(){
        return "main";
    }

    @DeleteMapping("/items/{itemId}")
    public String deleteItems(@PathVariable Long itemId){
        log.info("itemId={} Deleted", itemId);
        return "redirect:/admin/items";
    }

    @GetMapping("/orders")
    public String getOrders(@RequestParam(required = false) Long orderId,
                            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        Page<OrderResponse> orders;

        if (orderId != null) {
            Optional<OrderResponse> optionalOrder = orderService.searchOrder(orderId);
            if (optionalOrder.isEmpty()) {
                model.addAttribute("message", "해당 주문 번호가 존재하지 않습니다.");
                orders = Page.empty(); // orders 초기화
            } else {
                orders = new PageImpl<>(List.of(optionalOrder.get()), pageable, 1);
            }
        } else {
            orders = orderService.getAllOrders(pageable);
            if (orders.isEmpty()) {
                model.addAttribute("message", "주문이 없습니다.");
            }
        }
        model.addAttribute("orders", orders);
        return "orders/order-list";
    }


    // 주문 상세 조회 페이지
    @GetMapping("/order/{orderId}")
    public String orderSearch(@PathVariable Long orderId, Model model) {
        OrderResponse order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "orders/order-detail";
    }

    //관리자 - 주문 상태 변환 메서드
    //PUT이 안되기 때문에 POST로
    @PostMapping("/order/{orderId}/status")
    public String changeOrderStatus(
            @PathVariable Long orderId,
            @RequestParam("status") Status status
    ) {
        orderService.changeOrderStatus(orderId, status);
        return "redirect:/admin/orders";
    }

}

