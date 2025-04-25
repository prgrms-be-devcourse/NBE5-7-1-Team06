package kr.co.programmers.cafe.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptMailSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    // 표준 ISO 포맷(yyyy-MM-dd)
    public static final DateTimeFormatter DASHED = DateTimeFormatter.ISO_LOCAL_DATE;
    // 기본 ISO 기본 포맷(yyyyMMdd)
    public static final DateTimeFormatter BASIC  = DateTimeFormatter.BASIC_ISO_DATE;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${custom.mail.subject}")
    private String subject;

    /**
     * 구매 내역을 메일로 전송
     * @param receiptSend 메일로 전송할 주문 정보
     */
    public void sendReceiptMail(ReceiptMailSendRequest receiptSend) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 템플릿 엔진에 넘겨줄 구매 내역 Context 작성
            Context context = new Context();

            String orderId = receiptSend.getOrderedAt().format(BASIC) + receiptSend.getOrderId();
            context.setVariable("orderId", orderId);

            String orderedAt = receiptSend.getOrderedAt().format(DASHED);
            context.setVariable("orderedAt", orderedAt);

            context.setVariable("address", receiptSend.getAddress());
            context.setVariable("zipCode", receiptSend.getZipCode());
            context.setVariable("items", receiptSend.getItems());
            context.setVariable("totalPrice", receiptSend.getTotalPrice());

            // 타임리프 템플릿과 구매 내역 Context로 메일 본문 내용 생성
            String body = templateEngine.process("mail/mail-template", context);

            // mime 메시지에 수신자 이메일 주소, 메일 제목, 메일 본문 추가
            mimeMessageHelper.setTo(receiptSend.getMailAddress());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            // 메일 전송
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("이메일 전송 오류 : " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
