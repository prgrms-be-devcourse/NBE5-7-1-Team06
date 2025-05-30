package kr.co.programmers.cafe.domain.order.dao;

import kr.co.programmers.cafe.domain.order.entity.Order;
import kr.co.programmers.cafe.domain.order.entity.Status;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    List<Order> findByOrderedAtGreaterThanEqualAndStatus(LocalDateTime start, Status status);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.item"})
    Optional<Order> findById(Long id);
}


