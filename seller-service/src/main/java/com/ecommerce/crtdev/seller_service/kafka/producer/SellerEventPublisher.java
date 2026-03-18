package com.ecommerce.crtdev.seller_service.kafka.producer;

import com.ecommerce.crtdev.seller_service.kafka.events.CloudEvent;
import com.ecommerce.crtdev.seller_service.kafka.events.EventMetadata;
import com.ecommerce.crtdev.seller_service.kafka.events.SellerCreated;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SellerEventPublisher {

    private static final Logger log   = LoggerFactory.getLogger(SellerEventPublisher.class);
    private static final String TOPIC = "sellers.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SellerEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper  = objectMapper;
    }

    public void publishSellerCreated(Long userId) {
        try {
            CloudEvent<SellerCreated> envelope = new CloudEvent<>(
                    EventMetadata.of("SellerCreated"),
                    new SellerCreated(userId)
            );
            String json = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(TOPIC, String.valueOf(userId), json);
            log.info("Published SellerCreated for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to publish SellerCreated for userId={}", userId, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}