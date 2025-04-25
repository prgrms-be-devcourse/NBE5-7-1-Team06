package kr.co.programmers.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime orderedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

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

    //상태 변경 메서드
    public void changeStatus(Status newStatus) {
        this.status = newStatus;
    }
}
