package com.ecommerce.crtdev.catalog_service.domain.events.produces;

import java.math.BigDecimal;

public record ProductCreated (Long sellerId, String productId, String name, String description, String category, BigDecimal price, int stock) {}
