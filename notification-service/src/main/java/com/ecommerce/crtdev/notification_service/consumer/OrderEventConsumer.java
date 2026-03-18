package com.ecommerce.crtdev.notification_service.consumer;

import com.ecommerce.crtdev.notification_service.event.*;
import com.ecommerce.crtdev.notification_service.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private static final String REASON_REFUNDED = "Refunded";
    private static final String REASON_PAYMENT_PREFIX = "Payment";

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "orders.events", groupId = "notification-orders-group")
    public void onOrderEvent(String message) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(
                            CloudEvent.class, Object.class));

            String eventType = envelope.metadata().eventType();

            if ("OrderConfirmed".equals(eventType)) {
                OrderConfirmedEvent event = objectMapper.convertValue(
                        envelope.payload(), OrderConfirmedEvent.class);

                log.info("Sending confirmation email to={} orderId={} correlationId={}",
                        event.buyerEmail(), event.orderId(),
                        envelope.metadata().correlationId());

                emailService.sendOrderConfirmed(event.buyerEmail(), event.orderId());

            } else if ("OrderCancelled".equals(eventType)) {
                OrderCancelledEvent event = objectMapper.convertValue(
                        envelope.payload(), OrderCancelledEvent.class);

                log.info("Sending cancellation email to={} orderId={} reason={}",
                        event.buyerEmail(), event.orderId(), event.reason());

                routeCancellation(event);

            } else {
                log.debug("Ignoring order event type={}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to process order event for notification: {}", message, e);
            throw new RuntimeException("Notification event processing failed", e);
        }
    }

    private void routeCancellation(OrderCancelledEvent event) {
        String reason = event.reason() != null ? event.reason() : "";

        if (REASON_REFUNDED.equalsIgnoreCase(reason)) {
            emailService.sendOrderRefunded(event.buyerEmail(), event.orderId());

        } else if (reason.startsWith(REASON_PAYMENT_PREFIX)) {
            emailService.sendOrderCancelledPaymentFailed(
                    event.buyerEmail(), event.orderId(), reason);

        } else {
            emailService.sendOrderCancelledByUser(event.buyerEmail(), event.orderId());
        }
    }
}
