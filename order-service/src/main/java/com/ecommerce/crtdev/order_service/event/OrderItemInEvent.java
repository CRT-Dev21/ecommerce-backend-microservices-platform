package com.ecommerce.crtdev.order_service.event;

public record OrderItemInEvent(String productId, Long sellerId, int quantity) {}