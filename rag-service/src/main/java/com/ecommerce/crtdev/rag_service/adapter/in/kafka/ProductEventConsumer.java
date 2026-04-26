package com.ecommerce.crtdev.rag_service.adapter.in.kafka;

import com.ecommerce.crtdev.rag_service.adapter.in.kafka.events.CloudEvent;
import com.ecommerce.crtdev.rag_service.adapter.in.kafka.events.ProductCreated;
import com.ecommerce.crtdev.rag_service.application.port.in.IndexProductUseCase;
import com.ecommerce.crtdev.rag_service.domain.ProductDocument;
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
        CloudEvent<ProductCreated> event = mapper.readValue(json, new TypeReference<>(){});

        switch (event.metadata().eventType()){
            case "ProductCreated" -> indexProductUseCase.index(
                    new ProductDocument(
                            event.payload().productId(),
                            event.payload().name(),
                            event.payload().description(),
                            event.payload().category(),
                            event.payload().price()));
            case "ProductDeleted" -> indexProductUseCase.delete(event.payload().productId());
        }
    }
}