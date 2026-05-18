package com.ecommerce.crtdev.catalog_service.application.queries;

import java.math.BigDecimal;
import java.util.Optional;

public record SearchProductsQuery(
        Optional<String> searchTerm,
        Optional<String> categoryId,
        Optional<BigDecimal> minPrice,
        Optional<BigDecimal> maxPrice,
        Optional<String> lastId,
        int size
) {
    public String cacheKey() {
        return String.join("|",
                searchTerm.orElse(""),
                categoryId.orElse(""),
                minPrice.map(BigDecimal::toPlainString).orElse(""),
                maxPrice.map(BigDecimal::toPlainString).orElse(""),
                lastId.orElse(""),
                String.valueOf(size)
        );
    }
}