package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.commands.DeleteProductCommand;
import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.events.EventMetadata;
import com.ecommerce.crtdev.catalog_service.domain.events.produces.ProductDeleted;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;

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

    public void execute(DeleteProductCommand command) {

        productCache.evictProduct(command.productId());

        productRepository.deleteById(command.productId());

        eventPublisher.publishEvent(command.productId(), buildEvent(command.productId()));
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
