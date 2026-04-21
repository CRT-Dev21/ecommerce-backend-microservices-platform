package com.ecommerce.crtdev.catalog_service.application.queries;

public record ProductResponse (
        String productId,
        String name,
        String description,
        double price,
        String imageUrl
) {}
