package com.ecommerce.crtdev.inventory_service.kafka.events;

public record ProductCreated (Long sellerId, String productId, int stock) {}
