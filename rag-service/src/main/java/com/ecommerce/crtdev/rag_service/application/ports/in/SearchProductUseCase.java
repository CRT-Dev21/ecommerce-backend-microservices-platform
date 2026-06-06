package com.ecommerce.crtdev.rag_service.application.ports.in;

import com.ecommerce.crtdev.rag_service.domain.model.SearchStreamEvent;
import reactor.core.publisher.Flux;

public interface SearchProductUseCase {
    Flux<SearchStreamEvent> search(String userQuery);
}
