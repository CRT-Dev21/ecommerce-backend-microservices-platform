package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.commands.CreateProductCommand;
import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.events.EventMetadata;
import com.ecommerce.crtdev.catalog_service.domain.events.produces.ProductCreated;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class CreateProductHandler {
    private final IProductRepository productRepository;
    private final IEventPublisher eventPublisher;
    private final IFileStorage fileStorage;

    public CreateProductHandler(IProductRepository productRepository, IFileStorage fileStorage, IEventPublisher eventPublisher) {
        this.fileStorage = fileStorage;
        this.eventPublisher = eventPublisher;
        this.productRepository = productRepository;
    }

    public Mono<Void> execute(CreateProductCommand command){
        return fileStorage.storeImage(command.image())
                .flatMap(imageUrl -> {
                    Product product = new Product(
                            command.sellerId(),
                            command.name(),
                            command.description(),
                            command.price(),
                            command.stock(),
                            command.categoryId(),
                            imageUrl
                    );

                    return productRepository.save(product);
                }).flatMap(savedProduct -> {
                    CloudEvent<ProductCreated> event = buildEvent(savedProduct);
                    return eventPublisher.publishEvent(savedProduct.getId(), event);
                });
    }

    private CloudEvent<ProductCreated> buildEvent(Product product){
        ProductCreated payload = new ProductCreated(
                product.getSellerId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());

        EventMetadata metadata = new EventMetadata(
                UUID.randomUUID(),
                "ProductCreated",
                "1.0",
                "catalog-service",
                Instant.now());

        return new CloudEvent<>(metadata, payload);
    }
}
