package com.ecommerce.crtdev.catalog_service.application.queries;

import java.math.BigDecimal;
import java.util.Optional;

public record SearchProductsQuery(
        Optional<String> searchTerm,
        Optional<String> categoryId,
        Optional<BigDecimal> minPrice,
        Optional<BigDecimal> maxPrice,
        int page,
        int size
) {}