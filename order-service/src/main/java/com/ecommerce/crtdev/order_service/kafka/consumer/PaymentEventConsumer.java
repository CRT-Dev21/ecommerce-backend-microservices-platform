package com.ecommerce.crtdev.order_service.kafka.consumer;

import com.ecommerce.crtdev.order_service.entity.ProcessedEvent;
import com.ecommerce.crtdev.order_service.event.CloudEvent;
import com.ecommerce.crtdev.order_service.event.payment.PaymentFailedEvent;
import com.ecommerce.crtdev.order_service.event.payment.PaymentRefundedEvent;
import com.ecommerce.crtdev.order_service.event.payment.PaymentSuccessEvent;
import com.ecommerce.crtdev.order_service.repository.ProcessedEventRepository;
import com.ecommerce.crtdev.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderService orderService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(OrderService orderService,
                                ProcessedEventRepository processedEventRepository,
                                ObjectMapper objectMapper) {
        this.orderService             = orderService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper             = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = "payments.events", groupId = "order-payments-group")
    public void onPaymentEvent(String message) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(
                            CloudEvent.class, Object.class));

            if (isAlreadyProcessed(envelope)) return;

            String eventType = envelope.metadata().eventType();
            UUID incomingEventId = envelope.metadata().eventId();

            if ("PaymentSuccess".equals(eventType)) {
                PaymentSuccessEvent e = objectMapper.convertValue(
                        envelope.payload(), PaymentSuccessEvent.class);
                log.info("PaymentSuccess for order {} correlationId={}",
                        e.orderId(), envelope.metadata().correlationId());
                orderService.handlePaymentSuccess(e.orderId(), incomingEventId);

            } else if ("PaymentFailed".equals(eventType)) {
                PaymentFailedEvent e = objectMapper.convertValue(
                        envelope.payload(), PaymentFailedEvent.class);
                log.info("PaymentFailed for order {} reason={} correlationId={}",
                        e.orderId(), e.reason(), envelope.metadata().correlationId());
                orderService.handlePaymentFailed(e.orderId(), e.reason(), incomingEventId);

            } else if ("PaymentRefunded".equals(eventType)) {
                PaymentRefundedEvent e = objectMapper.convertValue(
                        envelope.payload(), PaymentRefundedEvent.class);
                log.info("PaymentRefunded for order {} correlationId={}",
                        e.orderId(), envelope.metadata().correlationId());
                orderService.handlePaymentRefunded(e.orderId(), incomingEventId);

            } else {
                log.debug("Ignoring payment event: {}", eventType);
                return;
            }

            markAsProcessed(envelope);

        } catch (Exception e) {
            log.error("Failed to process payment event: {}", message, e);
            throw new RuntimeException("Payment event processing failed", e);
        }
    }

    private boolean isAlreadyProcessed(CloudEvent<?> event) {
        boolean dup = processedEventRepository.existsByEventId(
                event.metadata().eventId());
        if (dup) log.warn("Duplicate event ignored: {} id={} correlationId={}",
                event.metadata().eventType(),
                event.metadata().eventId(),
                event.metadata().correlationId());
        return dup;
    }

    private void markAsProcessed(CloudEvent<?> event) {
        processedEventRepository.save(new ProcessedEvent(
                event.metadata().eventId(), event.metadata().eventType()));
    }
}