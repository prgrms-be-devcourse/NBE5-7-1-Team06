package kr.co.programmers.cafe.domain.order.dao;

import kr.co.programmers.cafe.domain.order.entity.Order;
import kr.co.programmers.cafe.domain.order.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " + // OrderItem 즉시 로딩
            "WHERE o.orderedAt >= :start " +
            "AND o.status = :status")
    List<Order> findOrdersWithItemsByTimeAndStatus(
            @Param("start") LocalDateTime start,
            @Param("status") Status status
    );
}
