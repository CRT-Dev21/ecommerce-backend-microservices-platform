package com.ecommerce.crtdev.order_service.event.catalog;

public record ProductCreated(
        String productId,
        Long sellerId,
        String name,
        double price
) {}
