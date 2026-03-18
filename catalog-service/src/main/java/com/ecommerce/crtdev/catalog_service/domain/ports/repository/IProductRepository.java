package com.ecommerce.crtdev.catalog_service.domain.ports.repository;

import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Mono<Void> deleteById(String id);
    Flux<Product> search(SearchProductsQuery query);
    Flux<Product> findByCategory(String categoryId);
    Flux<Product> findLatestProducts(int limit);
}
