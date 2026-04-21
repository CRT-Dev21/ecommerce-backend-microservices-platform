package com.ecommerce.crtdev.catalog_service.domain.ports.repository;

import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;

import java.util.List;
import java.util.Optional;


public interface IProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    void deleteById(String id);
    List<Product> search(SearchProductsQuery query);
    List<Product> findByCategory(String categoryId);
    List<Product> findLatestProducts(int limit);
}
