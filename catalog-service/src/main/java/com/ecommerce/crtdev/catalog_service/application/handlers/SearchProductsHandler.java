package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import reactor.core.publisher.Flux;

public class SearchProductsHandler {
    private final IProductRepository productRepository;

    public SearchProductsHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<ProductResponse> execute(SearchProductsQuery query) {
        return productRepository.search(query)
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getImageUrl()
                ))
                .onErrorResume(e -> Flux.error(new RuntimeException("Error searching products")));
    }
}
