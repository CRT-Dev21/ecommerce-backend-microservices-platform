package com.ecommerce.crtdev.rag_service.adapter.in.rest;

import com.ecommerce.crtdev.rag_service.adapter.in.rest.dto.SearchRequest;
import com.ecommerce.crtdev.rag_service.application.port.in.SearchProductsUseCase;
import com.ecommerce.crtdev.rag_service.domain.SearchResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class RagController {

    private final SearchProductsUseCase searchProductsUseCase;

    public RagController(SearchProductsUseCase searchProductsUseCase) {
        this.searchProductsUseCase = searchProductsUseCase;
    }

    @PostMapping("/search")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchProductsUseCase.search(request.query()));
    }
}