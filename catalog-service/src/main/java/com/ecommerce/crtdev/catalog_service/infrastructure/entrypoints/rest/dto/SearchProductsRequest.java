package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto;

import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

public record SearchProductsRequest(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
) {}