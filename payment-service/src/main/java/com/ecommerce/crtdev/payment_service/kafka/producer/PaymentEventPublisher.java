package com.ecommerce.crtdev.payment_service.kafka.producer;

import com.ecommerce.crtdev.payment_service.kafka.CloudEvent;
import com.ecommerce.crtdev.payment_service.kafka.EventMetadata;
import com.ecommerce.crtdev.payment_service.kafka.event.payment.PaymentFailedEvent;
import com.ecommerce.crtdev.payment_service.kafka.event.payment.PaymentRefundedEvent;
import com.ecommerce.crtdev.payment_service.kafka.event.payment.PaymentSuccessEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentEventPublisher {

    private static final Logger log   = LoggerFactory.getLogger(PaymentEventPublisher.class);
    private static final String TOPIC = "payments.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper  = objectMapper;
    }

    public void publishPaymentSuccess(String orderId, UUID correlationId,
                                      UUID checkoutId, UUID causationId) {
        EventMetadata metadata = EventMetadata.saga(
                "PaymentSuccess", correlationId, checkoutId, causationId);
        publish(metadata, orderId, new PaymentSuccessEvent(orderId));
    }

    public void publishPaymentFailed(String orderId, String reason,
                                     UUID correlationId, UUID checkoutId, UUID causationId) {
        EventMetadata metadata = EventMetadata.saga(
                "PaymentFailed", correlationId, checkoutId, causationId);
        publish(metadata, orderId, new PaymentFailedEvent(orderId, reason));
    }

    public void publishPaymentRefunded(String orderId, UUID correlationId,
                                       UUID checkoutId, UUID causationId) {
        EventMetadata metadata = EventMetadata.saga(
                "PaymentRefunded", correlationId, checkoutId, causationId);
        publish(metadata, orderId, new PaymentRefundedEvent(orderId));
    }

    private void publish(EventMetadata metadata, String key, Object payload) {
        try {
            CloudEvent<Object> envelope = new CloudEvent<>(metadata, payload);
            String json = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(TOPIC, key, json);
            log.info("Published {} orderId={} correlationId={} causationId={}",
                    metadata.eventType(), key,
                    metadata.correlationId(), metadata.causationId());
        } catch (Exception e) {
            log.error("Failed to publish {} for orderId={}", metadata.eventType(), key, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}