package com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.rest;

import com.ecommerce.crtdev.rag_service.application.ports.in.SearchProductUseCase;
import com.ecommerce.crtdev.rag_service.domain.model.SearchStreamEvent;
import com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.rest.dto.SearchRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final SearchProductUseCase searchProductsUseCase;

    public RagController(SearchProductUseCase searchProductsUseCase) {
        this.searchProductsUseCase = searchProductsUseCase;
    }

    @PostMapping(value = "/search", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SearchStreamEvent> search(@RequestBody SearchRequest request) {
        return searchProductsUseCase.search(request.query());
    }
}