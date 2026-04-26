package com.ecommerce.crtdev.rag_service.application.port.in;

import com.ecommerce.crtdev.rag_service.domain.SearchResult;

public interface SearchProductsUseCase {
    SearchResult search(String userQuery);
}
