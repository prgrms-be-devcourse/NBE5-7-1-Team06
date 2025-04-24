package kr.co.programmers.cafe.domain.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.programmers.cafe.domain.mail.dto.Item;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptSend;
import kr.co.programmers.cafe.domain.mail.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MailServiceTest {
    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Autowired
    private MailService mailService;

    private final String TARGET_EMAIL = "test@test.com";
    private final String SUBJECT = "IPv6 커피 구매 내역";

    private ReceiptSend createDummyDto() {
        List<Item> items = new ArrayList<>();
        int totalPrice = 0;
        int itemCount = (int) (Math.random() * 10) + 1;

        // 랜덤 구매 상품 추가
        for (int i = 0; i < itemCount; i++) {
            int quantity = (int) (Math.random() * 10) + 1;
            int price = (int) (Math.random() * 1000);

            items.add(
                    Item.builder().name("아메리카노" + i).quantity(quantity).price(price).build()
            );

            totalPrice += price * quantity;
        }

        return ReceiptSend.builder()
                .targetAddress(TARGET_EMAIL)
                .subject(SUBJECT)
                .orderId("20250423308")
                .orderedAt("2025-04-23")
                .address("천안시 서북구 카페로 12-3")
                .zipCode("12345")
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    @Test
    @DisplayName("메일 전송 테스트")
    void successfullySendMail() throws MessagingException {
        // given - 랜덤 구매 목록 생성
        ReceiptSend dto = createDummyDto();

        // when - 구매 내역 메일 전송
        // 로컬 서버로 메일 전송
        mailService.sendReceiptMail(dto);

        // 비동기로 샌드박스 메일 서버에서 메일 수신 대기
        greenMail.waitForIncomingEmail(5000, 1);

        // then - 메일로 수신받은 구매 내역에서 각 상품 가격의 소계, 전체 상품 가격의 총계 확인
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        MimeMessage receivedMessage = receivedMessages[0];

        // 수신자 메일 주소 검사
        String target = receivedMessage.getAllRecipients()[0].toString();
        assertThat(target).isEqualTo(TARGET_EMAIL);

        // 메일 제목 검사
        String subject = receivedMessage.getSubject();
        assertThat(subject).isEqualTo(SUBJECT);

        // 메일 내용 중 각 상품 가격의 소계 검사
        String body = GreenMailUtil.getBody(receivedMessage);
        NumberFormat numberFormat = new DecimalFormat("#,###");
        for (Item item : dto.getItems()) {
            assertThat(body).contains(numberFormat.format(item.getPrice() * item.getQuantity()));
        }

        // 메일 내용 중 전체 상품 가격의 총계 검사
        assertThat(body).contains(numberFormat.format(dto.getTotalPrice()));
    }
}