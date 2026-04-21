package com.ecommerce.crtdev.catalog_service.domain.events.produces;

public record ProductPriceChanged(String productId, double newPrice) {}
