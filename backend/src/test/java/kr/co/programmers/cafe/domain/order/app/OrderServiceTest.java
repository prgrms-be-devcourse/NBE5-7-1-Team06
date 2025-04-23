package kr.co.programmers.cafe.domain.order.app;

import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
import kr.co.programmers.cafe.domain.order.dto.OrderItemRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.entity.Category;
import kr.co.programmers.cafe.domain.order.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class OrderServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
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

        log.info("Starting createOrderTest...");

        OrderRequest request = new OrderRequest("test@email.com", "Test Address", "00000",
                List.of(
                        new OrderItemRequest(1L, 2),
                        new OrderItemRequest(2L, 3)
                )
        );

        // POST 요청
        var result = mockMvc.perform(post("/api/order")
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
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isNotEmpty();

        log.info("createOrder로 생성된 주문 ID: {}", result.getResponse().getContentAsString());
    }
}