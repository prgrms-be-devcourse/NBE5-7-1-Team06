package kr.co.programmers.cafe.domain.order.app;


import jakarta.transaction.Transactional;
import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
import kr.co.programmers.cafe.domain.order.dao.OrderRepository;
import kr.co.programmers.cafe.domain.order.dto.OrderItemResponse;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.entity.Item;
import kr.co.programmers.cafe.domain.order.entity.Order;
import kr.co.programmers.cafe.domain.order.entity.OrderItem;
import kr.co.programmers.cafe.domain.order.entity.Status;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문을 생성하고 생성된 주문의 ID를 반환합니다.
     * 사용자 예외 처리로 수정 예정
     * @param request 사용자로부터 받은 주문 요청
     * @return 생성된 주문의 ID
     */
    @Transactional
    public Long createOrder(OrderRequest request) {

        // 사용자로부터 받은 주문 요청에서 주문 아이템 목록을 추출하여 OrderItem 객체 리스트로 변환
        List<OrderItem> orderItemList = request.getOrderItemRequests().stream().map(itemRequest -> {
            // 아이템을 찾아서, 해당 아이템과 수량으로 새로운 OrderItem을 생성
            Item findItem = itemRepository.findById(itemRequest.getItemId()).orElseThrow(
                    () -> new ItemNotFoundException(itemRequest.getItemId()) // 아이템을 찾을 수 없으면 예외 발생
            );
            return new OrderItem(findItem, itemRequest.getQuantity());
        }).toList();

        // 주문 아이템 리스트에서 각 아이템의 가격 * 수량을 곱해서 총 가격 계산
        int totalPriceService = orderItemList.stream().mapToInt(
                orderItem -> orderItem.getItem().getPrice() * orderItem.getQuantity()
        ).sum();

        Integer totalPriceClient = request.getTotalPrice();

        if (!isEqualPrice(totalPriceService, totalPriceClient)) {
            throw new IllegalArgumentException("총 가격이 서버 계산 값과 일치하지 않습니다.");
        }

        // order 엔티티 생성
        Order order = Order.builder()
                .email(request.getEmail())
                .address(request.getAddress())
                .zipCode(request.getZipCode())
                .orderItems(orderItemList)
                .totalPrice(totalPriceService)
                .build();

        // 각 OrderItem 객체에 생성된 주문을 할당
        orderItemList.forEach(orderItem -> orderItem.assignOrder(order));

        // 만든 주문을 DB에 저장하고, 생성된 주문의 ID를 반환
        return orderRepository.save(order).getId();
    }

    private boolean isEqualPrice(Integer clientPrice, int servicePrice) {
        return clientPrice != null && clientPrice == servicePrice;
    }

    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다. "));

        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .name(orderItem.getItem().getName())
                        .price(orderItem.getItem().getPrice())
                        .quantity(orderItem.getQuantity())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .email(order.getEmail())
                .address(order.getAddress())
                .zipCode(order.getZipCode())
                .orderItems(orderItemResponses)
                .totalPrice(order.getTotalPrice())
                .orderedAt(order.getOrderedAt())
                .status(order.getStatus())
                .build();
    }

    @Transactional
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        return ordersPage.map(order -> OrderResponse.builder()
                .orderId(order.getId())
                .email(order.getEmail())
                .address(order.getAddress())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemResponse.builder()
                                .name(orderItem.getItem().getName())
                                .price(orderItem.getItem().getPrice())
                                .quantity(orderItem.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    // 목록 단건 조회용
    @Transactional
    public Optional<OrderResponse> searchOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> OrderResponse.builder()
                        .orderId(order.getId())
                        .email(order.getEmail())
                        .address(order.getAddress())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .orderItems(order.getOrderItems().stream()
                                .map(orderItem -> OrderItemResponse.builder()
                                        .name(orderItem.getItem().getName())
                                        .quantity(orderItem.getQuantity())
                                        .build())
                                .toList())
                        .build());
    }

    @Transactional
    public void changeOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        order.changeStatus(status);
    }


}

