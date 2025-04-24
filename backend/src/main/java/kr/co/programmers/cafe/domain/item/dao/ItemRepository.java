package kr.co.programmers.cafe.domain.item.dao;

import kr.co.programmers.cafe.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
