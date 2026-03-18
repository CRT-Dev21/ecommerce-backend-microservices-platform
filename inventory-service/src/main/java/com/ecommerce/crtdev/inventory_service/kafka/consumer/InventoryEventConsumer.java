package com.ecommerce.crtdev.inventory_service.kafka.consumer;

import com.ecommerce.crtdev.inventory_service.kafka.CloudEvent;
import com.ecommerce.crtdev.inventory_service.kafka.events.OrderConfirmedEvent;
import com.ecommerce.crtdev.inventory_service.kafka.events.ProductCreated;
import com.ecommerce.crtdev.inventory_service.kafka.events.ProductDeleted;
import com.ecommerce.crtdev.inventory_service.kafka.events.ReverseStock;
import com.ecommerce.crtdev.inventory_service.model.ProcessedEvent;
import com.ecommerce.crtdev.inventory_service.repository.ProcessedEventRepository;
import com.ecommerce.crtdev.inventory_service.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class InventoryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final InventoryService inventoryService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public InventoryEventConsumer(
            InventoryService inventoryService,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper) {
        this.inventoryService         = inventoryService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper             = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = "catalog.events", groupId = "inventory-catalog-group")
    public void onCatalogEvent(String message) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(CloudEvent.class, Object.class)
            );

            if (isAlreadyProcessed(envelope)) return;

            String eventType = envelope.metadata().eventType();

            if ("ProductCreated".equals(eventType)) {
                ProductCreated event = objectMapper.convertValue(envelope.payload(), ProductCreated.class);
                log.info("Creating inventory for product {}", event.productId());
                inventoryService.createInventory(event.productId(), event.sellerId(), event.stock());

            } else if ("ProductDeleted".equals(eventType)) {
                ProductDeleted event = objectMapper.convertValue(envelope.payload(), ProductDeleted.class);
                log.info("Deleting inventory for product {}", event.productId());
                inventoryService.deleteInventory(event.productId());

            } else {
                log.debug("Ignoring unknown catalog event type: {}", eventType);
                return;
            }

            markAsProcessed(envelope);

        } catch (Exception e) {
            log.error("Failed to process catalog event: {}", message, e);
            throw new RuntimeException("Catalog event processing failed", e);
        }
    }

    @Transactional
    @KafkaListener(topics = "orders.events", groupId = "inventory-orders-group")
    public void onOrderEvent(String message) {
        try {
            CloudEvent<Object> envelope = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(CloudEvent.class, Object.class)
            );

            if (isAlreadyProcessed(envelope)) return;

            String eventType = envelope.metadata().eventType();

            if ("ReverseStock".equals(eventType)) {
                ReverseStock event = objectMapper.convertValue(envelope.payload(), ReverseStock.class);
                log.info("Reversing stock for order {} — {} items", event.orderId(), event.items().size());
                inventoryService.reverseAll(event.items());

            } else if ("OrderConfirmed".equals(eventType)) {
                OrderConfirmedEvent event = objectMapper.convertValue(envelope.payload(), OrderConfirmedEvent.class);
                log.info("Confirming reservations for order {} — {} items", event.orderId(), event.items().size());
                inventoryService.confirmAll(event.items());

            } else {
                log.debug("Ignoring unknown order event type: {}", eventType);
                return;
            }

            markAsProcessed(envelope);

        } catch (Exception e) {
            log.error("Failed to process order event: {}", message, e);
            throw new RuntimeException("Order event processing failed", e);
        }
    }

    // idempotency
    private boolean isAlreadyProcessed(CloudEvent<?> event) {
        boolean duplicate = processedEventRepository.existsByEventId(
                event.metadata().eventId());
        if (duplicate) {
            log.warn("Duplicate event ignored: {} id={}",
                    event.metadata().eventType(),
                    event.metadata().eventId());
        }
        return duplicate;
    }

    private void markAsProcessed(CloudEvent<?> event) {
        processedEventRepository.save(new ProcessedEvent(
                event.metadata().eventId(),
                event.metadata().eventType()
        ));
    }
}
