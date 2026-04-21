package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stock
) {}
