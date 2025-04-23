package kr.co.programmers.cafe.domain.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    //    private final AdminService adminService;

    // 로그인 실패의 경우 error 를 로그인 페이지로 넘겨주기 위한 메서드
    @GetMapping("/login")
    public String adminLogin(@RequestParam(required = false) String error, Model model) {
        if(error != null) {
            model.addAttribute("errorMessage", "올바르지 않은 계정입니다!");
            log.error("로그인 실패");
        }
        log.info("로그인 페이지");
        return "admin/login-form";
    }

    // 로그인 성공 테스트 용
    @GetMapping("/manage")
    public String adminManage() {
        log.info("관리 페이지 호출 테스트 - 로그인 성공");
        return "admin/manage-form";
    }
}
