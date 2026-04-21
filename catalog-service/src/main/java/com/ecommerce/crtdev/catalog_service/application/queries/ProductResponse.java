package com.ecommerce.crtdev.catalog_service.application.queries;

import java.math.BigDecimal;

public record ProductResponse (
        String productId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
