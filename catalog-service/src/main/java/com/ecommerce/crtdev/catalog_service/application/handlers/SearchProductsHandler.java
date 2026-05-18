package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public class SearchProductsHandler {
    private final IProductRepository productRepository;

    public SearchProductsHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "product-search", key = "#query.cacheKey()",
            condition = "#query.searchTerm().isEmpty()")
    public List<ProductResponse> execute(SearchProductsQuery query) {
        return productRepository.search(query)
                .stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(),
                        p.getDescription(), p.getPrice(), p.getImageUrl()))
                .toList();
    }
}
