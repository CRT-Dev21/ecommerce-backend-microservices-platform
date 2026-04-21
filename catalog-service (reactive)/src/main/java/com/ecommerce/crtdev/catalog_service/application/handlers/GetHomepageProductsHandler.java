package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.GetHomepageProductsQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import reactor.core.publisher.Flux;

public class GetHomepageProductsHandler {

    private final IProductRepository repository;

    public GetHomepageProductsHandler(IProductRepository repository) {
        this.repository = repository;
    }

    public Flux<ProductResponse> execute(GetHomepageProductsQuery query) {
        return repository.findLatestProducts(query.limit())
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getImageUrl()
                ));
    }
}
