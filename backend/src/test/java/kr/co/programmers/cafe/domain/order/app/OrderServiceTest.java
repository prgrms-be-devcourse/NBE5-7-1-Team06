package kr.co.programmers.cafe.domain.order.app;


import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import kr.co.programmers.cafe.domain.item.app.ItemService;
import kr.co.programmers.cafe.domain.item.dao.ItemRepository;
import kr.co.programmers.cafe.domain.item.dto.ItemSimpleResponse;
import kr.co.programmers.cafe.domain.item.entity.Category;
import kr.co.programmers.cafe.domain.item.entity.Item;
import kr.co.programmers.cafe.domain.order.dto.OrderItemRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.entity.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class OrderServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderService orderService;

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
                  "totalPrice": 8500
                }
                """;

        // POST 요청
        var result = mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk())  // 응답 상태 코드가 200 OK인지 확인
                .andReturn();

        // 샌드박스 메일 서버로 메일 수신 여부 확인
        greenMail.waitForIncomingEmail(5000, 1);
        assertThat(greenMail.getReceivedMessages().length).isEqualTo(1);

        // 생성된 주문 ID 확인
        String orderId = result.getResponse().getContentAsString();
        assertThat(orderId).isNotEmpty();  // 주문 ID가 비어있지 않음을 확인

        log.info("createOrder로 생성된 주문 ID: {}", orderId);
    }


    @Test
    @DisplayName("전체 주문 조회 테스트")
    void getAllOrdersTest() {
        // given
        OrderRequest request1 = new OrderRequest(
                "user1@example.com", "Seoul", "11111",
                List.of(new OrderItemRequest(1L, 2)), 6000
        );

        OrderRequest request2 = new OrderRequest(
                "user2@example.com", "Busan", "22222",
                List.of(new OrderItemRequest(2L, 1)), 2500
        );

        Long orderId1 = orderService.createOrder(request1);
        Long orderId2 = orderService.createOrder(request2);

        // when
        List<OrderResponse> allOrders = orderService.getAllOrders();

        // then
        assertThat(allOrders).isEqualTo(2);
        assertThat(allOrders).extracting("orderId").contains(orderId1, orderId2);
        log.info(allOrders.toString());

    }

    @Test
    @DisplayName("주문 상세 조회 테스트")
    void getOrderByIdTest() {
        // given
        OrderRequest request = new OrderRequest(
                "user@example.com", "Incheon", "33333",
                List.of(new OrderItemRequest(2L, 3)), 7500
        );

        Long orderId = orderService.createOrder(request);

        // when
        OrderResponse order = orderService.getOrderById(orderId);

        // then
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getEmail()).isEqualTo("user@example.com");
        assertThat(order.getTotalPrice()).isEqualTo(7500);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("주문 상태 변경 테스트")
    void changeOrderStatusTest() {
        // given
        OrderRequest request = new OrderRequest(
                "user@example.com", "Daegu", "44444",
                List.of(new OrderItemRequest(1L, 2)), 6000
        );
        Long orderId = orderService.createOrder(request);

        // when
        orderService.changeOrderStatus(orderId, Status.COMPLETED);

        // then
        OrderResponse updatedOrder = orderService.getOrderById(orderId);
        assertThat(updatedOrder.getStatus()).isEqualTo(Status.COMPLETED);
    }

}