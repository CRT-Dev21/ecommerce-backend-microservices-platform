package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.messaging;

import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class KafkaEventPublisher implements IEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> publishEvent(String key, CloudEvent<?> event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> Mono.fromFuture(
                        kafkaTemplate.send("catalog.events", key, json).toCompletableFuture()
                ))
                .publishOn(Schedulers.boundedElastic())
                .then();
    }
}
