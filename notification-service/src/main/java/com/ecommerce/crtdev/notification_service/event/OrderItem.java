package com.ecommerce.crtdev.notification_service.event;

public record OrderItem(String productId, Long sellerId, int quantity) {}

