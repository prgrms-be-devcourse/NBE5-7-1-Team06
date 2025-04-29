package kr.co.programmers.cafe.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.co.programmers.cafe.domain.mail.dto.DeliveringMailSendRequest;
import kr.co.programmers.cafe.domain.mail.dto.ReceiptMailSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    // 표준 ISO 포맷(yyyy-MM-dd)
    private static final DateTimeFormatter DASHED = DateTimeFormatter.ISO_LOCAL_DATE;
    // 기본 ISO 기본 포맷(yyyyMMdd)
    private static final DateTimeFormatter BASIC  = DateTimeFormatter.BASIC_ISO_DATE;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${custom.mail.receiptSubject}")
    private String receiptSubject;

    @Value("${custom.mail.startDeliverySubject}")
    private String startDeliverySubject;


    public void sendReceiptMail(ReceiptMailSendRequest receiptRequest) {
        Context context = new Context();
        String orderId = receiptRequest.getSendTime().format(BASIC) + receiptRequest.getOrderId();
        context.setVariable("orderId", orderId);
        context.setVariable("orderedAt", receiptRequest.getSendTime().format(DASHED));
        context.setVariable("address", receiptRequest.getAddress());
        context.setVariable("zipCode", receiptRequest.getZipCode());
        context.setVariable("items", receiptRequest.getItems());
        context.setVariable("totalPrice", receiptRequest.getTotalPrice());

        sendTemplateMail(
                receiptRequest.getMailAddress(), receiptSubject, "mail/receipt-mail", context
        );
    }

    public void sendDeliveringMail(DeliveringMailSendRequest deliveringRequest) {
        Context context = new Context();
        context.setVariable("deliveryStartedAt", deliveringRequest.getSendTime().format(DASHED));
        context.setVariable("address", deliveringRequest.getAddress());
        context.setVariable("zipCode", deliveringRequest.getZipCode());
        context.setVariable("items", deliveringRequest.getItems());
        context.setVariable("totalPrice", deliveringRequest.getTotalPrice());

        sendTemplateMail(
                deliveringRequest.getMailAddress(), startDeliverySubject, "mail/delivering-mail", context
        );
    }

    @Async
    protected void sendTemplateMail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String body = templateEngine.process(templateName, context);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("이메일 전송 오류 : " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void sendDeliveringMails(Collection<DeliveringMailSendRequest> deliveringRequests) {
        // JavaMailSender가 thread-safe하기 때문에, 메일을 병렬 처리 방식으로 보낼 수 있다.
        deliveringRequests.parallelStream().forEach(this::sendDeliveringMail);
    }
}
