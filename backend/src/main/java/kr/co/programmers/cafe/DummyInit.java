package kr.co.programmers.cafe;

import jakarta.annotation.PostConstruct;
import kr.co.programmers.cafe.domain.item.dao.ItemRepository;
import kr.co.programmers.cafe.domain.item.entity.Category;
import kr.co.programmers.cafe.domain.item.entity.Item;
import kr.co.programmers.cafe.domain.order.dao.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyInit {

    private final ItemRepository itemRepository;

    @PostConstruct
    public void init() {
        Item item1 = Item.builder()
                .name("아메리카노")
                .price(4000)
                .category(Category.A)
                .build();

        itemRepository.save(item1);

        Item item2 = Item.builder()
                .name("카페라떼")
                .price(5000)
                .category(Category.A)
                .build();

        itemRepository.save(item2);

        Item item3 = Item.builder()
                .name("카푸치노")
                .price(4500)
                .category(Category.A)
                .build();

        itemRepository.save(item3);

        Item item4 = Item.builder()
                .name("카라멜마끼야또")
                .price(6000)
                .category(Category.A)
                .build();

        itemRepository.save(item4);

        Item item5 = Item.builder()
                .name("아포가토")
                .price(8000)
                .category(Category.A)
                .build();

        itemRepository.save(item5);

        Item item6 = Item.builder()
                .name("에스프레소")
                .price(3000)
                .category(Category.A)
                .build();

        itemRepository.save(item6);

        Item item7 = Item.builder()
                .name("레몬에이드")
                .price(4000)
                .category(Category.B)
                .build();

        itemRepository.save(item7);

        Item item8 = Item.builder()
                .name("자몽에이드")
                .price(4000)
                .category(Category.B)
                .build();

        itemRepository.save(item8);

        Item item9 = Item.builder()
                .name("청포도에이드")
                .price(4000)
                .category(Category.B)
                .build();

        itemRepository.save(item9);

        Item item10 = Item.builder()
                .name("청귤에이드")
                .price(4000)
                .category(Category.B)
                .build();

        itemRepository.save(item10);
    }
}
