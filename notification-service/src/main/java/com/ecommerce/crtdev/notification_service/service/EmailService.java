package com.ecommerce.crtdev.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender       mailSender;
    private final EmailTemplateService templateService;

    @Value("${notification.mail.from}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        EmailTemplateService templateService) {
        this.mailSender      = mailSender;
        this.templateService = templateService;
    }

    public void sendOrderConfirmed(String to, String orderId) {
        send(to, "Your order has been confirmed", templateService.orderConfirmed(orderId));
    }

    public void sendOrderCancelledByUser(String to, String orderId) {
        send(to, "Your order has been cancelled", templateService.orderCancelledByUser(orderId));
    }

    public void sendOrderCancelledPaymentFailed(String to, String orderId, String reason) {
        send(to, "Payment failed — order cancelled",
                templateService.orderCancelledPaymentFailed(orderId, reason));
    }

    public void sendOrderRefunded(String to, String orderId) {
        send(to, "Your refund has been processed", templateService.orderRefunded(orderId));
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to={} subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to={} subject={}", to, subject, e);
        }
    }
}

