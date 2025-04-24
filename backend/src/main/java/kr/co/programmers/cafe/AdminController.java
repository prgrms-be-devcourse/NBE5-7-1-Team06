package kr.co.programmers.cafe;

import kr.co.programmers.cafe.temp.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/items")
    public String items(Model model) {
        Item item = Item.builder().name("상품1").price(1000).id(1L).category(Item.Category.Coffee).imageUrl("temp.png").build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        model.addAttribute("items", items);
        return "items/item_list";
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
