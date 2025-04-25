package kr.co.programmers.cafe.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long itemId) {
        super("해당 아이템을 찾을 수 없습니다. Not Found ID: " + itemId);
    }
}
