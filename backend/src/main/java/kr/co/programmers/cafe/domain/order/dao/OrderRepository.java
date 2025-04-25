package kr.co.programmers.cafe.domain.order.dao;

import kr.co.programmers.cafe.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "join fetch o.orderItems oi " +
            "join fetch oi.item " +
            "where o.id = :orderId")
    Optional<Order> searchById(@Param("orderId") Long id);
}

