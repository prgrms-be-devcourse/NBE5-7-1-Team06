package kr.co.programmers.cafe.domain.order.app;

import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
import kr.co.programmers.cafe.domain.order.dto.OrderItemRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.entity.Category;
import kr.co.programmers.cafe.domain.order.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        // 테스트용 아이템이 DB에 없으면 추가
        itemRepository.saveAll(List.of(
                Item.builder()
                        .name("Americano")
                        .price(3000)
                        .category(Category.A)
                        .image("img1.png")
                        .build(),
                Item.builder()
                        .name("Croissant")
                        .price(2500)
                        .category(Category.B)
                        .image("img2.png")
                        .build()
        ));
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrderTest() throws Exception {

        OrderRequest request = new OrderRequest("test@email.com", "Test Address", "00000",
                List.of(
                        new OrderItemRequest(1L, 2),
                        new OrderItemRequest(2L, 3)
                )
        );

        // when & then: MockMvc를 통해 POST 요청을 보내고 응답을 확인
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "email": "test@example.com",
                                  "address": "Test Address",
                                  "zipCode": "12345",
                                  "orderItemRequests": [
                                    { "itemId": 1, "quantity": 2 },
                                    { "itemId": 2, "quantity": 1 }
                                  ]
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());  // 반환된 ID가 비어있지 않음을 확인
    }
}