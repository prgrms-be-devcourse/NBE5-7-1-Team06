package kr.co.programmers.cafe.domain.order.app;


import jakarta.transaction.Transactional;
import kr.co.programmers.cafe.domain.order.dao.ItemRepository;
import kr.co.programmers.cafe.domain.order.dao.OrderRepository;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.entity.Item;
import kr.co.programmers.cafe.domain.order.entity.Order;
import kr.co.programmers.cafe.domain.order.entity.OrderItem;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        int totalPrice = orderItemList.stream().mapToInt(
                orderItem -> orderItem.getItem().getPrice() * orderItem.getQuantity()
        ).sum();

        // order 엔티티 생성
        Order order = Order.builder()
                .email(request.getEmail())
                .address(request.getAddress())
                .zipCode(request.getZipCode())
                .orderItems(orderItemList)
                .totalPrice(totalPrice)
                .build();

        // 각 OrderItem 객체에 생성된 주문을 할당
        orderItemList.forEach(orderItem -> orderItem.assignOrder(order));

        // 만든 주문을 DB에 저장하고, 생성된 주문의 ID를 반환
        return orderRepository.save(order).getId();
    }
}
