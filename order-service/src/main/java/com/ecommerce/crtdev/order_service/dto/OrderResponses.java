package com.ecommerce.crtdev.order_service.dto;

import com.ecommerce.crtdev.order_service.entity.Order;
import com.ecommerce.crtdev.order_service.entity.OrderItem;
import com.ecommerce.crtdev.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class OrderResponses {

    private OrderResponses() {}

    public record CheckoutResponse(
            UUID checkoutId,
            List<OrderSummaryResponse> ordersCreated,
            List<FailedOrderResponse>  ordersFailed
    ) {
        public boolean hasFailures()   { return !ordersFailed.isEmpty(); }
        public boolean hasSuccesses()  { return !ordersCreated.isEmpty(); }
    }

    public record FailedOrderResponse(Long sellerId, String reason) {}

    public record OrderSummaryResponse(
            UUID id,
            Long sellerId,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {
        public static OrderSummaryResponse from(Order order) {
            return new OrderSummaryResponse(
                    order.getId(),
                    order.getSellerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    order.getItems().stream().map(OrderItemResponse::from).toList()
            );
        }
    }

    public record OrderItemResponse(
            String productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal
    ) {
        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProductId(),
                    item.getProductName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
    }
}