package com.ecommerce.crtdev.payment_service.kafka.event.order;

public record OrderItem(String productId, Long sellerId, int quantity) {}
