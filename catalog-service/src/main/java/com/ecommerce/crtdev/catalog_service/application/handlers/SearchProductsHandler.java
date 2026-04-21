package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;

import java.util.List;

public class SearchProductsHandler {
    private final IProductRepository productRepository;

    public SearchProductsHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> execute(SearchProductsQuery query) {
        return productRepository.search(query).stream().map(this::mapToResponse).toList();
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
}
