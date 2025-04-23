package kr.co.programmers.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    /**
     * 추후 주문 생성 시간(orderedAt)과 조합하여 커스텀 주문 번호 생성 예정
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private LocalDateTime orderedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Status status = Status.ORDERED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private Integer totalPrice;

    @Builder
    public Order(String email, String address, String zipCode, List<OrderItem> orderItems, Integer totalPrice) {
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }
}
