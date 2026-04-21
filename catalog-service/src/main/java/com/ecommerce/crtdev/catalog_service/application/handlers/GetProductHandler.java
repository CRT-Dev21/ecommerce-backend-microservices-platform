package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.GetProductQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.exception.ProductNotFoundException;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;

public class GetProductHandler {
    private final IProductRepository productRepository;
    private final IProductCache productCache;

    public GetProductHandler(IProductRepository productRepository, IProductCache productCache) {
        this.productRepository = productRepository;
        this.productCache = productCache;
    }

    public ProductResponse execute(GetProductQuery query) {
        return productCache.getProduct(query.productId())
                .map(this::mapToResponse)
                .orElseGet(() -> findInDbAndCache(query.productId()));
    }

    private ProductResponse findInDbAndCache(String productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    productCache.putProduct(product);
                    return mapToResponse(product);
                })
                .orElseThrow(() -> new ProductNotFoundException(productId));
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
