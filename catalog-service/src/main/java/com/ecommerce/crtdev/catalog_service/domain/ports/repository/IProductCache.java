package com.ecommerce.crtdev.catalog_service.domain.ports.repository;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;

import java.util.Optional;

public interface IProductCache {
    Optional<Product> getProduct(String productId);
    void putProduct(Product product);
    void evictProduct(String productId);
}
