package kr.co.programmers.cafe.domain.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.programmers.cafe.domain.mail.dto.ItemMailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.MailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptMailSendRequest;
import kr.co.programmers.cafe.domain.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class MailServiceTest {
    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@test.com", "test"))
            .withPerMethodLifecycle(false);;

    @Autowired
    private MailService mailService;

    private final String TARGET_EMAIL = "test@test.com";
    private final String TARGET_ADDRESS = "천안시 서북구 카페로 12-3";
    private final String TARGET_ZIPCODE = "12345";

    // 표준 ISO 포맷(yyyy-MM-dd)
    private final DateTimeFormatter DASHED = DateTimeFormatter.ISO_LOCAL_DATE;
    // 기본 ISO 기본 포맷(yyyyMMdd)
    private final DateTimeFormatter BASIC  = DateTimeFormatter.BASIC_ISO_DATE;

    private List<ItemMailSendRequest> createDummyItems(int itemCount) {
        List<ItemMailSendRequest> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            int quantity = (int) (Math.random() * 10) + 1;
            int price = (int) (Math.random() * 1000);

            items.add(
                    ItemMailSendRequest.builder().name("아메리카노" + i).quantity(quantity).price(price).build()
            );
        }

        return items;
    }

    private ReceiptMailSendRequest createDummyReceiptMailDto() {
        List<ItemMailSendRequest> items = createDummyItems((int) (Math.random() * 5 + 1));
        int totalPrice = items.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();

        return ReceiptMailSendRequest.builder()
                .mailAddress(TARGET_EMAIL)
                .orderId(308L)
                .sendTime(LocalDateTime.now())
                .address(TARGET_ADDRESS)
                .zipCode(TARGET_ZIPCODE)
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    private Message getReceivedMessage() throws MessagingException {
        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        return receivedMessages[0];
    }

    /**
     * Quoted Printable 인코딩 되어 있기 때문에, '=' 문자를 넣고 개행하는 경우가 있다.
     * 때문에, 각 라인 마지막에 '=' 문자를 지우고 두 줄을 합치는 작업을 수행하여 메일 본문을 얻는다.
     */
    private String getMessageBody(Message message) throws MessagingException {
        return GreenMailUtil.getBody(message).replaceAll("(?m)=\\r?\\n", "");
    }

    private void verifyCommonMailProperties(Message receivedMessage, String body, MailSendRequest request) throws MessagingException {
        // 수신자 메일 주소 검사
        String target = receivedMessage.getAllRecipients()[0].toString();
        assertThat(target).isEqualTo(TARGET_EMAIL);

        // 메일 전송 시간 검사
        String sendTime = request.getSendTime().format(DASHED);
        assertThat(body).containsIgnoringNewLines(sendTime);

        NumberFormat numberFormat = new DecimalFormat("#,###");

        // 각 상품 가격의 소계 검사
        for (ItemMailSendRequest item : request.getItems()) {
            assertThat(body).containsIgnoringNewLines(
                    numberFormat.format(item.getPrice() * item.getQuantity())
            );
        }

        // 총 가격 검사
        assertThat(body).containsIgnoringNewLines(numberFormat.format(request.getTotalPrice()));
    }

    @Test
    @DisplayName("단일 구매 내역 메일 전송 테스트")
    void sendReceiptMailTest() throws MessagingException {
        // given - 랜덤 구매 목록 생성
        ReceiptMailSendRequest dto = createDummyReceiptMailDto();

        // when - 구매 내역 메일 전송
        // 로컬 서버로 메일 전송
        mailService.sendReceiptMail(dto);

        // 비동기로 샌드박스 메일 서버에서 메일 수신 대기
        greenMail.waitForIncomingEmail(5000, 1);

        // then - 메일로 수신받은 구매 내역에서 각 상품 가격의 소계, 전체 상품 가격의 총계 확인
        Message receivedMessage = getReceivedMessage();
        String body = getMessageBody(receivedMessage);

        // 공통 속성 검증
        verifyCommonMailProperties(receivedMessage, body, dto);

        // 주문 ID 검사
        String orderId = dto.getSendTime().format(BASIC) + dto.getOrderId();
        assertThat(body).containsIgnoringNewLines(orderId);
    }

}