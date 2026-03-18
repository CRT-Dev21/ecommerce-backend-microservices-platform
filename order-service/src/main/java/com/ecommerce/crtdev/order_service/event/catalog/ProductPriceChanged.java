package com.ecommerce.crtdev.order_service.event.catalog;

public record ProductPriceChanged(String productId, double newPrice) {}