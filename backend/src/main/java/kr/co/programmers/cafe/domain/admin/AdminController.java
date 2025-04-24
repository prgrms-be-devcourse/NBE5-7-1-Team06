package kr.co.programmers.cafe.domain.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    //    private final AdminService adminService;

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
}
