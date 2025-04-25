package kr.co.programmers.cafe.domain.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.programmers.cafe.domain.mail.dto.ItemMailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptMailSendRequest;
import kr.co.programmers.cafe.domain.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class MailServiceTest {
    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private MailService mailService;

    private final String TARGET_EMAIL = "test@test.com";

    // 표준 ISO 포맷(yyyy-MM-dd)
    public final DateTimeFormatter DASHED = DateTimeFormatter.ISO_LOCAL_DATE;
    // 기본 ISO 기본 포맷(yyyyMMdd)
    public final DateTimeFormatter BASIC  = DateTimeFormatter.BASIC_ISO_DATE;

    private ReceiptMailSendRequest createDummyDto() {
        List<ItemMailSendRequest> items = new ArrayList<>();
        int totalPrice = 0;
        int itemCount = (int) (Math.random() * 10) + 1;

        // 랜덤 구매 상품 추가
        for (int i = 0; i < itemCount; i++) {
            int quantity = (int) (Math.random() * 10) + 1;
            int price = (int) (Math.random() * 1000);

            items.add(
                    ItemMailSendRequest.builder().name("아메리카노" + i).quantity(quantity).price(price).build()
            );

            totalPrice += price * quantity;
        }

        return ReceiptMailSendRequest.builder()
                .mailAddress(TARGET_EMAIL)
                .orderId(308L)
                .orderedAt(LocalDateTime.now())
                .address("천안시 서북구 카페로 12-3")
                .zipCode("12345")
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    @Test
    @DisplayName("구매 내역 메일 전송 테스트")
    void successfullySendMail() throws MessagingException {
        // given - 랜덤 구매 목록 생성
        ReceiptMailSendRequest dto = createDummyDto();

        // when - 구매 내역 메일 전송
        // 로컬 서버로 메일 전송
        mailService.sendReceiptMail(dto);

        // 비동기로 샌드박스 메일 서버에서 메일 수신 대기
        greenMail.waitForIncomingEmail(5000, 1);

        // then - 메일로 수신받은 구매 내역에서 각 상품 가격의 소계, 전체 상품 가격의 총계 확인
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        Message receivedMessage = receivedMessages[0];

        // 수신자 메일 주소 검사
        String target = receivedMessage.getAllRecipients()[0].toString();
        assertThat(target).isEqualTo(TARGET_EMAIL);

        // Quoted Printable 인코딩 되어 있기 때문에, '=' 문자를 넣고 개행하는 경우가 있다.
        // 때문에, 각 라인 마지막에 '=' 문자를 지우고 두 줄을 합치는 작업을 수행하여 메일 본문을 얻는다.
        String body = GreenMailUtil.getBody(receivedMessage).replaceAll("(?m)=\\r?\\n", "");

        // 주문 ID 검사
        String orderId = dto.getOrderedAt().format(BASIC) + dto.getOrderId();
        assertThat(body).containsIgnoringNewLines(orderId);

        // 주문 시간 검사
        String orderedAt = dto.getOrderedAt().format(DASHED);
        assertThat(body).containsIgnoringNewLines(orderedAt);

        // 메일 내용 중 각 상품 가격의 소계 검사
        NumberFormat numberFormat = new DecimalFormat("#,###");
        for (ItemMailSendRequest item : dto.getItems()) {
            assertThat(body).containsIgnoringNewLines(
                    numberFormat.format(item.getPrice() * item.getQuantity())
            );
        }

        // 메일 내용 중 전체 상품 가격의 총계 검사
        assertThat(body).containsIgnoringNewLines(numberFormat.format(dto.getTotalPrice()));
    }
}