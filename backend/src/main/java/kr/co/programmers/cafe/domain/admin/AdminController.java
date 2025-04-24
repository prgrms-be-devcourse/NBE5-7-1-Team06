package kr.co.programmers.cafe.domain.admin;

import kr.co.programmers.cafe.domain.item.app.ItemService;
import kr.co.programmers.cafe.domain.item.dto.ItemCreateForm;
import kr.co.programmers.cafe.domain.item.dto.ItemEditForm;
import kr.co.programmers.cafe.domain.item.dto.ItemResponse;
import kr.co.programmers.cafe.domain.order.app.OrderService;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.entity.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final OrderService orderService;

    // 로그인 실패의 경우 error 를 로그인 페이지로 넘겨주기 위한 메서드
    @GetMapping("/login")
    public String adminLogin(@RequestParam(required = false) String error, Model model) {
        log.info("로그인 페이지");
        if (error != null) {
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

    @GetMapping("/main")
    public String adminMain() {
        return "admin/main";
    }

    @GetMapping("/items")
    public String readItems(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("items", itemService.findAll(PageRequest.of(page, 10)));
        return "admin/items/item-list";
    }

    @GetMapping("/items/new")
    public String createItemForm(ItemCreateForm itemCreateForm) {
        return "admin/items/item-new";
    }

    @PostMapping("/items")
    public String createItem(ItemCreateForm itemCreateForm) {
        log.info("아이템 생성 시도");
        itemService.create(itemCreateForm);
        log.info("아이템 생성 성공");
        return "redirect:/admin/items";
    }

    @GetMapping("/items/edit/{itemId}")
    public String editItemForm(@PathVariable Long itemId, ItemEditForm itemEditForm) {
        ItemResponse itemResponse = itemService.findById(itemId);
        itemEditForm.setId(itemId);
        itemEditForm.setName(itemResponse.getName());
        itemEditForm.setPrice(itemResponse.getPrice());
        itemEditForm.setCategory(itemResponse.getCategory().name());
        itemEditForm.setImageUrl(itemResponse.getImageName());
        return "admin/items/item-edit";
    }

    @PatchMapping("/items/edit/{itemId}")
    public String editItem(@PathVariable Long itemId, ItemEditForm itemEditForm) {
        itemEditForm.setId(itemId);
        itemService.edit(itemEditForm);
        return "redirect:/admin/items";
    }

    @DeleteMapping("/items/{itemId}")
    public String deleteItem(@PathVariable Long itemId) {
        itemService.delete(itemId);
        return "redirect:/admin/items";
    }

    @GetMapping("/orders")
    public String orderList(Model model) {
        List<OrderResponse> allOrders = orderService.getAllOrders();
        model.addAttribute("orders", allOrders);
        return "admin/orders";
    }

    // 주문 상세 조회 페이지
    @GetMapping("/order/{orderId}")
    public String orderSearch(@PathVariable Long orderId, Model model) {
        OrderResponse order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "admin/order-search";
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
