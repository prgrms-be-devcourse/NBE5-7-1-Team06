package kr.co.programmers.cafe.domain.order.app;


import jakarta.transaction.Transactional;
import kr.co.programmers.cafe.domain.item.dao.ItemRepository;
import kr.co.programmers.cafe.domain.item.entity.Item;
import kr.co.programmers.cafe.domain.mail.dto.DeliveringMailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.ItemMailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptMailSendRequest;
import kr.co.programmers.cafe.domain.mail.service.MailService;
import kr.co.programmers.cafe.domain.order.dao.OrderRepository;
import kr.co.programmers.cafe.domain.order.dto.OrderItemResponse;
import kr.co.programmers.cafe.domain.order.dto.OrderRequest;
import kr.co.programmers.cafe.domain.order.dto.OrderResponse;
import kr.co.programmers.cafe.domain.order.entity.Order;
import kr.co.programmers.cafe.domain.order.entity.OrderItem;
import kr.co.programmers.cafe.domain.order.entity.Status;
import kr.co.programmers.cafe.global.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final MailService mailService;

    /**
     * 주문을 생성하고 생성된 주문의 ID를 반환합니다.
     * 사용자 예외 처리로 수정 예정
     *
     * @param request 사용자로부터 받은 주문 요청
     * @return 생성된 주문의 ID
     */
    @Transactional
    public Long createOrder(OrderRequest request) {

        // 주문 내역 상품 DTO 목록
        List<ItemMailSendRequest> itemMailSendRequests = new ArrayList<>();

        // 사용자로부터 받은 주문 요청에서 주문 아이템 목록을 추출하여 OrderItem 객체 리스트로 변환
        List<OrderItem> orderItemList = request.getOrderItemRequests().stream().map(itemRequest -> {
            // 아이템을 찾아서, 해당 아이템과 수량으로 새로운 OrderItem을 생성
            Item findItem = itemRepository.findById(itemRequest.getItemId()).orElseThrow(
                    () -> new ItemNotFoundException(itemRequest.getItemId()) // 아이템을 찾을 수 없으면 예외 발생
            );

            itemMailSendRequests.add(ItemMailSendRequest.builder()
                    .name(findItem.getName())
                    .price(findItem.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .build());

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

        // 만든 주문을 DB에 저장
        orderRepository.save(order);

        // 주문 내역 DTO
        ReceiptMailSendRequest receiptMailSendRequest = ReceiptMailSendRequest.builder()
                .orderId(order.getId())
                .mailAddress(order.getEmail())
                .sendTime(order.getOrderedAt())
                .zipCode(order.getZipCode())
                .address(order.getAddress())
                .items(itemMailSendRequests)
                .totalPrice(order.getTotalPrice())
                .build();

        // 주문 내역 메일 전송
        mailService.sendReceiptMail(receiptMailSendRequest);

        return order.getId();
    }

    private boolean isEqualPrice(Integer clientPrice, int servicePrice) {
        return clientPrice != null && clientPrice == servicePrice;
    }

    // 주문 날짜(yyyyMMdd)와 주문 ID를 결합하여 문자열을 Long으로 변환
    private Long getFormattedOrderId(Order order) {

        String formattedDate = order.getOrderedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedOrderId = formattedDate + order.getId();

        return Long.parseLong(formattedOrderId);
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
                .orderId(getFormattedOrderId(order))  // 실제 DB의 주문 ID
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
                .orderId(getFormattedOrderId(order))
                .email(order.getEmail())
                .address(order.getAddress())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build());
    }

    @Transactional
    public Optional<OrderResponse> searchOrder(Long orderId) {

        if (String.valueOf(orderId).length() < 9) {
            return Optional.empty(); // 주문 ID가 9자리 미만인 경우는 "존재하지 않음" 처리
        }

        String formattedOrderId = String.valueOf(orderId);
        Long orderIdPart = Long.valueOf(formattedOrderId.substring(8));

        // 주문을 찾을 수 없으면 Optional.empty() 반환
        return orderRepository.findById(orderIdPart).map(order -> OrderResponse.builder()
                .orderId(getFormattedOrderId(order))
                .email(order.getEmail())
                .address(order.getAddress())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemResponse.builder()
                                .name(orderItem.getItem().getName())
                                .quantity(orderItem.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }

    @Transactional
    public void changeOrderStatus(Long orderId, Status status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        order.changeStatus(status);
    }

    /**
     * 주문 배송 스케줄링 메소드
     * 매일 오후 두 시에 주문 배송 진행
     */
    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    @Transactional
    public void executeOrderDelivery() {
        List<Order> foundOrders = orderRepository.findByOrderedAtGreaterThanEqualAndStatus(
                LocalDateTime.now(), Status.ORDERED
        );

        // 조회된 주문들의 상태를 COMPLETED(배송 중)으로 변경
        for (Order order : foundOrders) {
            order.changeStatus(Status.COMPLETED);
        }

        // 메일 발송 단위를 구분하기 위한 그룹 기준 (email + address + zipCode)
        record Key (String email, String address, String zipCode) {}

        // 같은 메일 주소, 배송 주소, 우편 번호를 가지는 주문들을 그룹화
        Map<Key, List<Order>> grouped = foundOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> new Key(o.getEmail(), o.getAddress(), o.getZipCode())
                ));

        // 각 그룹에 대해 배송 시작 안내 메일 전송 요청 DTO 생성
        List<DeliveringMailSendRequest> deliveringMailSendRequests = grouped.entrySet().stream()
                .map(e -> {
                    Key key = e.getKey();
                    List<Order> orders = e.getValue();

                    // 주문에 포함된 상품들을 Item ID 기준으로 묶어 수량 합산
                    Map<Long, Integer> orderItems = orders.stream()
                            .flatMap(o -> o.getOrderItems().stream())
                            .collect(Collectors.groupingBy(
                                    oi -> oi.getItem().getId(),
                                    Collectors.summingInt(OrderItem::getQuantity)
                            ));

                    // 총 결제 금액 계산 및 ItemMailSendRequest 목록 구성
                    // 람다 내부에서는 final 변수만 사용 가능하기 때문에, int나 Integer 대신에 AtomicInteger 사용
                    AtomicInteger totalPrice = new AtomicInteger();
                    List<ItemMailSendRequest> itemMailSendRequests = orderItems.entrySet().stream()
                            .map(oi -> {
                                // 위에서 Item ID 기준으로 묶은 상품의 정보를 검색 후 DTO 생성
                                Long itemId = oi.getKey();
                                Item item = itemRepository.findById(itemId)
                                        .orElseThrow(NoSuchElementException::new);
                                Integer quantity = oi.getValue();

                                totalPrice.getAndAdd(item.getPrice() * quantity);

                                return ItemMailSendRequest.builder()
                                        .name(item.getName())
                                        .price(item.getPrice())
                                        .quantity(quantity)
                                        .build();
                            })
                            .toList();

                    // 배송 시작 안내 메일 전송 요청 DTO 생성
                    return DeliveringMailSendRequest.builder()
                            .mailAddress(key.email)
                            .address(key.address)
                            .zipCode(key.zipCode)
                            .sendTime(LocalDateTime.now())
                            .totalPrice(totalPrice.get())
                            .items(itemMailSendRequests)
                            .build();
                })
                .toList();

        // 배송 시작 안내 메일 전송
        mailService.sendDeliveringMails(deliveringMailSendRequests);
    }
}
