package kr.co.programmers.cafe.domain.order.app;

import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
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
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        // 미리 주문 가능한 아이템들을 저장
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

        // JSON 데이터로 요청 본문 작성
        String requestBody = """
                {
                  "email": "test@example.com",
                  "address": "Test Address",
                  "zipCode": "12345",
                  "orderItemRequests": [
                    { "itemId": 1, "quantity": 2 },
                    { "itemId": 2, "quantity": 1 }
                  ],
                  "totalPrice": 10500
                }
                """;

        // POST 요청
        var result = mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())  // 응답 상태 코드가 200 OK인지 확인
                .andReturn();

        // 생성된 주문 ID 확인
        String orderId = result.getResponse().getContentAsString();
        assertThat(orderId).isNotEmpty();  // 주문 ID가 비어있지 않음을 확인

        log.info("createOrder로 생성된 주문 ID: {}", orderId);
    }
}