package com.ecommerce.crtdev.order_service.kafka.publisher;

import com.ecommerce.crtdev.order_service.entity.Order;
import com.ecommerce.crtdev.order_service.event.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderEventPublisher {
    private static final Logger log   = LoggerFactory.getLogger(OrderEventPublisher.class);
    private static final String TOPIC = "orders.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper  = objectMapper;
    }

    public void publishOrderReadyForPayment(Order order, UUID checkoutId){
        OrderReadyForPaymentEvent payload = new OrderReadyForPaymentEvent(
                order.getId(), order.getUserId(), order.getSellerId(),
                order.getTotalAmount(), order.getPaymentMethodToken()
        );

        EventMetadata metadata = EventMetadata.saga(
                "OrderReadyForPayment", "order-service",
                order.getId(),
                checkoutId,
                null
        );
        publish(metadata, order.getId().toString(), payload);
    }

    public void publishOrderConfirmed(Order order, UUID checkoutId, UUID causationId) {
        OrderConfirmedEvent payload = new OrderConfirmedEvent(
                order.getId(), order.getBuyerEmail(), toEventItems(order));
        EventMetadata metadata = EventMetadata.saga(
                "OrderConfirmed", "order-service",
                order.getId(), checkoutId, causationId
        );
        publish(metadata, order.getId().toString(), payload);
    }

    public void publishReverseStock(Order order, UUID checkoutId, UUID causationId) {
        ReverseStockEvent payload = new ReverseStockEvent(
                order.getId(), toEventItems(order));
        EventMetadata metadata = EventMetadata.saga(
                "ReverseStock", "order-service",
                order.getId(), checkoutId, causationId
        );
        publish(metadata, order.getId().toString(), payload);
    }

    public void publishOrderCancelled(Order order, UUID checkoutId,
                                      UUID causationId, String reason) {
        OrderCancelledEvent payload = new OrderCancelledEvent(
                order.getId(), order.getBuyerEmail(), reason);
        EventMetadata metadata = EventMetadata.saga(
                "OrderCancelled", "order-service",
                order.getId(), checkoutId, causationId
        );
        publish(metadata, order.getId().toString(), payload);
    }

    public void publishPaymentRefundRequest(Order order, UUID checkoutId) {
        PaymentRefundRequestEvent payload = new PaymentRefundRequestEvent(
                order.getId(), order.getUserId(),
                order.getBuyerEmail(), order.getTotalAmount()
        );
        EventMetadata metadata = EventMetadata.saga(
                "PaymentRefundRequest", "order-service",
                order.getId(), checkoutId, null
        );
        publish(metadata, order.getId().toString(), payload);
    }

    // helpers

    private List<OrderItemInEvent> toEventItems(Order order) {
        return order.getItems().stream()
                .map(i -> new OrderItemInEvent(
                        i.getProductId(), order.getSellerId(), i.getQuantity()))
                .toList();
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
            log.error("Failed to publish {} for order {}", metadata.eventType(), key, e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}
