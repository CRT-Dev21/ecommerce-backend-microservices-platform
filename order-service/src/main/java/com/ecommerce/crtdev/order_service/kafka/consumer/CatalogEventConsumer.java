package com.ecommerce.crtdev.order_service.kafka.consumer;

import com.ecommerce.crtdev.order_service.entity.ProcessedEvent;
import com.ecommerce.crtdev.order_service.event.CloudEvent;
import com.ecommerce.crtdev.order_service.event.catalog.ProductCreated;
import com.ecommerce.crtdev.order_service.event.catalog.ProductDeleted;
import com.ecommerce.crtdev.order_service.event.catalog.ProductPriceChanged;
import com.ecommerce.crtdev.order_service.repository.ProcessedEventRepository;
import com.ecommerce.crtdev.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CatalogEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CatalogEventConsumer.class);

    private final OrderService orderService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public CatalogEventConsumer(OrderService orderService,
                                ProcessedEventRepository processedEventRepository,
                                ObjectMapper objectMapper) {
        this.orderService             = orderService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper             = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = "catalog.events", groupId = "order-catalog-group")
    public void onCatalogEvent(String message) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(
                            CloudEvent.class, Object.class));

            if (isAlreadyProcessed(envelope)) return;

            String eventType = envelope.metadata().eventType();

            if ("ProductCreated".equals(eventType)) {
                ProductCreated e = objectMapper.convertValue(
                        envelope.payload(), ProductCreated.class);
                orderService.upsertSnapshot(e.productId(), e.sellerId(),
                        e.name(), BigDecimal.valueOf(e.price()));

            } else if ("ProductDeleted".equals(eventType)) {
                ProductDeleted e = objectMapper.convertValue(
                        envelope.payload(), ProductDeleted.class);
                orderService.deactivateSnapshot(e.productId());

            } else if ("ProductPriceChanged".equals(eventType)) {
                ProductPriceChanged e = objectMapper.convertValue(
                        envelope.payload(), ProductPriceChanged.class);
                orderService.updateSnapshotPrice(e.productId(),
                        BigDecimal.valueOf(e.newPrice()));

            } else {
                log.debug("Ignoring catalog event: {}", eventType);
                return;
            }

            markAsProcessed(envelope);

        } catch (Exception e) {
            log.error("Failed to process catalog event: {}", message, e);
            throw new RuntimeException("Catalog event processing failed", e);
        }
    }

    private boolean isAlreadyProcessed(CloudEvent<?> event) {
        boolean dup = processedEventRepository.existsByEventId(
                event.metadata().eventId());
        if (dup) log.warn("Duplicate event ignored: {} id={}",
                event.metadata().eventType(), event.metadata().eventId());
        return dup;
    }

    private void markAsProcessed(CloudEvent<?> event) {
        processedEventRepository.save(new ProcessedEvent(
                event.metadata().eventId(), event.metadata().eventType()));
    }
}
