package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.GetHomepageProductsQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;

import java.util.List;

public class GetHomepageProductsHandler {

    private final IProductRepository repository;

    public GetHomepageProductsHandler(IProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductResponse> execute(GetHomepageProductsQuery query) {
        return repository.findLatestProducts(query.limit()).stream().map(this::mapToResponse).toList();
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
