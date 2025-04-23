package kr.co.programmers.cafe.domain.order.dao;

import kr.co.programmers.cafe.domain.order.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
