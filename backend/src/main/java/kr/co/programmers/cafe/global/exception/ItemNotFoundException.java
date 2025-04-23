package kr.co.programmers.cafe.global.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long itemId) {
        super("해당 아이템을 찾을 수 없습니다. Not Found ID: " + itemId);
    }
}
