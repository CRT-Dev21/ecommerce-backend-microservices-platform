package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.queries.GetProductQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.exception.ProductNotFoundException;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import reactor.core.publisher.Mono;

public class GetProductHandler {
    private final IProductRepository productRepository;
    private final IProductCache productCache;

    public GetProductHandler(IProductRepository productRepository, IProductCache productCache){
        this.productRepository = productRepository;
        this.productCache = productCache;
    }

    public Mono<ProductResponse> execute (GetProductQuery query){
        return productCache.getProduct(query.productId())
                .switchIfEmpty(
                        productRepository.findById(query.productId())
                                .flatMap(product ->
                                        productCache.putProduct(product)
                                                .thenReturn(product)
                                )
                )
                .switchIfEmpty(Mono.error(new ProductNotFoundException(query.productId())))
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getImageUrl()
                ));
    }
}
