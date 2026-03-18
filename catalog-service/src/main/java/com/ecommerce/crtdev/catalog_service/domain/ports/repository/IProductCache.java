package com.ecommerce.crtdev.catalog_service.domain.ports.repository;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import reactor.core.publisher.Mono;

public interface IProductCache {
    Mono<Product> getProduct(String productId);
    Mono<Void> putProduct(Product product);
    Mono<Void> evictProduct(String productId);
}
