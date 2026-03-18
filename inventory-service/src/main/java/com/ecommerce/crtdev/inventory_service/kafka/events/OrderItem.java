package com.ecommerce.crtdev.inventory_service.kafka.events;

public record OrderItem(String productId, int quantity) {}