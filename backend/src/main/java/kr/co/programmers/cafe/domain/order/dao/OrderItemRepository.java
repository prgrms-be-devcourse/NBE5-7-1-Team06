package kr.co.programmers.cafe.domain.order.dao;

import kr.co.programmers.cafe.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
