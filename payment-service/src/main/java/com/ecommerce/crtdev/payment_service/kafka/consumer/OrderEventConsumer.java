package com.ecommerce.crtdev.payment_service.kafka.consumer;

import com.ecommerce.crtdev.payment_service.entity.ProcessedEvent;
import com.ecommerce.crtdev.payment_service.kafka.CloudEvent;
import com.ecommerce.crtdev.payment_service.kafka.event.order.OrderReadyForPaymentEvent;
import com.ecommerce.crtdev.payment_service.kafka.event.order.PaymentRefundRequestEvent;
import com.ecommerce.crtdev.payment_service.repository.ProcessedEventRepository;
import com.ecommerce.crtdev.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper             objectMapper;

    public OrderEventConsumer(PaymentService paymentService,
                              ProcessedEventRepository processedEventRepository,
                              ObjectMapper objectMapper) {
        this.paymentService           = paymentService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper             = objectMapper;
    }

    @Transactional
    @KafkaListener(
            topics = "orders.events",
            groupId = "payment-group",
            // DLQ configurado en KafkaConfig — mensajes que fallen
            // después de los reintentos van a orders.events.payment.group.dlq
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderEvent(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(
                            CloudEvent.class, Object.class));

            if (isAlreadyProcessed(envelope)) return;

            String eventType     = envelope.metadata().eventType();
            UUID incomingEventId = envelope.metadata().eventId();
            UUID correlationId   = envelope.metadata().correlationId();
            UUID checkoutId      = envelope.metadata().checkoutId();

            if ("OrderReadyForPayment".equals(eventType)) {
                OrderReadyForPaymentEvent event = objectMapper.convertValue(
                        envelope.payload(), OrderReadyForPaymentEvent.class);

                log.info("Processing payment for orderId={} correlationId={}",
                        event.orderId(), correlationId);

                paymentService.processPayment(
                        event, incomingEventId, correlationId, checkoutId);

            } else if ("PaymentRefundRequest".equals(eventType)) {
                PaymentRefundRequestEvent event = objectMapper.convertValue(
                        envelope.payload(), PaymentRefundRequestEvent.class);

                log.info("Processing refund for orderId={} correlationId={}",
                        event.orderId(), correlationId);

                paymentService.processRefund(
                        event, incomingEventId, correlationId, checkoutId);

            } else {
                log.debug("Ignoring order event type={} offset={}", eventType, offset);
                return;
            }

            markAsProcessed(envelope);

        } catch (Exception e) {
            log.error("Failed to process order event topic={} offset={} message={}",
                    topic, offset, message, e);
            // Relanzar para que el DefaultErrorHandler con backoff reintente
            // Después de los reintentos el mensaje va al DLQ
            throw new RuntimeException("Order event processing failed", e);
        }
    }

    private boolean isAlreadyProcessed(CloudEvent<?> event) {
        boolean dup = processedEventRepository.existsByEventId(
                event.metadata().eventId());
        if (dup) log.warn("Duplicate event ignored type={} id={} correlationId={}",
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

