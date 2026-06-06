package com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.kafka;

import com.ecommerce.crtdev.rag_service.application.ports.in.IndexProductUseCase;
import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.kafka.events.CloudEvent;
import com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.kafka.events.ProductCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);
    private final IndexProductUseCase indexProductUseCase;
    private final ObjectMapper mapper;

    public ProductEventConsumer(IndexProductUseCase indexProductUseCase, ObjectMapper mapper) {
        this.indexProductUseCase = indexProductUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "catalog.events", groupId = "rag-service-group")
    public void consume(String json) {
        try {
            CloudEvent<ProductCreated> event = mapper.readValue(json, new TypeReference<>(){});

            if ("ProductCreated".equals(event.metadata().eventType())) {
                ProductDocument doc = new ProductDocument(
                        event.payload().productId(),
                        event.payload().name(),
                        event.payload().description(),
                        event.payload().category(),
                        event.payload().price());

                indexProductUseCase.index(doc)
                        .doOnSuccess(v -> log.info("Successfully indexed product: {}", doc.productId()))
                        .doOnError(e -> log.error("Failed to index product: {}", doc.productId(), e))
                        .subscribe();

            } else if ("ProductDeleted".equals(event.metadata().eventType())) {
                indexProductUseCase.delete(event.payload().productId())
                        .doOnSuccess(v -> log.info("Successfully deleted product: {}", event.payload().productId()))
                        .subscribe();
            }
        } catch (Exception e) {
            log.error("Error processing product event payload", e);
        }
    }
}