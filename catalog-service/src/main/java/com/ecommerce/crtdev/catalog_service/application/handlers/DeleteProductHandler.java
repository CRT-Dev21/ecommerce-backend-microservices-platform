package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.commands.DeleteProductCommand;
import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.events.EventMetadata;
import com.ecommerce.crtdev.catalog_service.domain.events.produces.ProductDeleted;
import com.ecommerce.crtdev.catalog_service.domain.exception.ProductNotFoundException;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class DeleteProductHandler {
    private final IProductRepository productRepository;
    private final IProductCache productCache;
    private final IEventPublisher eventPublisher;

    public DeleteProductHandler(IProductRepository productRepository, IProductCache productCache, IEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.productRepository = productRepository;
        this.productCache =productCache;
    }

    public Mono<Void> execute(DeleteProductCommand command) {
        return productRepository.findById(command.productId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException(command.productId())))
                .flatMap(product -> {
                    return productRepository.deleteById(product.getId())
                            .then(Mono.defer(() -> {
                                CloudEvent<ProductDeleted> event = buildEvent(product.getId());
                                return eventPublisher.publishEvent(product.getId(), event);
                            }))
                            .then(productCache.evictProduct(product.getId()));
                });
    }

    private CloudEvent<ProductDeleted> buildEvent(String productId){
        ProductDeleted payload = new ProductDeleted(productId);

        EventMetadata metadata = new EventMetadata(
                UUID.randomUUID(),
                "ProductDeleted",
                "1.0",
                "catalog-service",
                Instant.now()
        );

        return new CloudEvent<>(metadata, payload);
    }
}
