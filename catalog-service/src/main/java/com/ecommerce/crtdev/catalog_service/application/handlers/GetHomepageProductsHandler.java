package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.GetHomepageProductsQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public class GetHomepageProductsHandler {

    private final IProductRepository repository;

    public GetHomepageProductsHandler(IProductRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "homepage-products", key = "#query.limit()")
    public List<ProductResponse> execute(GetHomepageProductsQuery query) {
        return repository.findLatestProducts(query.limit())
                .stream()
                .map( p -> new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getImageUrl()))
                .toList();
    }
}
