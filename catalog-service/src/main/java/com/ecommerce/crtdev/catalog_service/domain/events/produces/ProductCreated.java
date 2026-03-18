package com.ecommerce.crtdev.catalog_service.domain.events.produces;

public record ProductCreated (Long sellerId, String productId, String name, double price, int stock) {}
