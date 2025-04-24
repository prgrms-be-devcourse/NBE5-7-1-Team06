package kr.co.programmers.cafe.domain.admin;

import kr.co.programmers.cafe.domain.item.ItemCreateForm;
import kr.co.programmers.cafe.domain.item.ItemEditForm;
import kr.co.programmers.cafe.domain.item.ItemResponse;
import kr.co.programmers.cafe.domain.item.ItemService;
import kr.co.programmers.cafe.global.util.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final FileManager fileManager;
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

    @GetMapping("/main")
    public String adminMain() {
        return "admin/main";
    }

    @GetMapping("/items")
    public String readItems(Model model){
        model.addAttribute("items", itemService.findAll());
        return "admin/items/item-list";
    }

    @GetMapping("/items/new")
    public String createItemForm(ItemCreateForm itemCreateForm){
        return "admin/items/item-new";
    }

    @PostMapping("/items")
    public String createItem(ItemCreateForm itemCreateForm){
        try {
            log.info("아이템 생성 시도");
            itemService.create(itemCreateForm);
        } catch( Exception e ){
            log.info("아이템 생성 실패");
            return "admin/items/item-new";
        }
        log.info("아이템 생성 성공");
        return "redirect:/admin/items";
    }

    @GetMapping("/display-image/{fileName}")
    public ResponseEntity<byte[]> displayImage(@PathVariable String fileName) throws IOException {
        byte[] imageBytes = fileManager.getFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 또는 적절한 미디어 타입

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/items/edit/{itemId}")
    public String editItemForm(@PathVariable Long itemId, ItemEditForm itemEditForm){
        ItemResponse itemResponse = itemService.findById(itemId);
        itemEditForm.setId(itemId);
        itemEditForm.setName(itemResponse.getName());
        itemEditForm.setPrice(itemResponse.getPrice());
        itemEditForm.setCategory(itemResponse.getCategory().name());
        itemEditForm.setImageUrl(itemResponse.getImageUrl());
        return "admin/items/item-edit";
    }

    @PatchMapping("/items/edit/{itemId}")
    public String editItem(@PathVariable Long itemId,ItemEditForm itemEditForm){
        itemEditForm.setId(itemId);
        try {
            log.info("아이템 수정 시도");
            itemService.edit(itemEditForm);
        } catch( Exception e ){
            log.info("아이템 생성 실패");
            return "admin/items/item-new";
        }
        log.info("아이템 생성 성공");
        return "redirect:/admin/items";
    }

    @DeleteMapping("/items/{itemId}")
    public String deleteItem(@PathVariable Long itemId){
        itemService.delete(itemId);
        return "redirect:/admin/items";
    }

}
