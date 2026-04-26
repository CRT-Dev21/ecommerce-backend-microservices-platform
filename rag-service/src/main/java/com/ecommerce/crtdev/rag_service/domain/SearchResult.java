package com.ecommerce.crtdev.rag_service.domain;

import java.util.List;

public record SearchResult(
        String answer,
        List<ProductDocument> sources
) {
}
