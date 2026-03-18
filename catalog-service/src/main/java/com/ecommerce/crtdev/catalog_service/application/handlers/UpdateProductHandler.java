package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.commands.UpdateProductCommand;
import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.events.EventMetadata;
import com.ecommerce.crtdev.catalog_service.domain.events.produces.ProductPriceChanged;
import com.ecommerce.crtdev.catalog_service.domain.exception.ProductNotFoundException;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public class UpdateProductHandler {
    private final IProductRepository productRepository;
    private final IProductCache productCache;
    private final IEventPublisher eventPublisher;
    private final IFileStorage fileStorage;

    public UpdateProductHandler(IProductRepository productRepository, IFileStorage fileStorage, IProductCache productCache, IEventPublisher eventPublisher) {
        this.fileStorage = fileStorage;
        this.productRepository = productRepository;
        this.productCache = productCache;
        this.eventPublisher = eventPublisher;
    }

    public Mono<Void> execute(UpdateProductCommand command) {

        return productRepository.findById(command.productId())

                .switchIfEmpty(Mono.error(new ProductNotFoundException(command.productId())))

                .flatMap(product -> {

                    boolean priceWillChange =
                            command.price().isPresent()
                                    && !command.price().get().equals(product.getPrice());

                    return handleImageUpdate(product, command)

                            .map(prod -> {
                                command.name().ifPresent(prod::setName);
                                command.description().ifPresent(prod::setDescription);
                                command.price().ifPresent(prod::setPrice);
                                command.stock().ifPresent(prod::setStock);
                                return prod;
                            })

                            .flatMap(productRepository::save)

                            .flatMap(savedProduct -> {

                                Mono<Void> eventMono = Mono.empty();

                                if (priceWillChange) {
                                    CloudEvent<ProductPriceChanged> event = buildEvent(savedProduct);
                                    eventMono = eventPublisher.publishEvent(savedProduct.getId(), event);
                                }

                                return eventMono
                                        .then(productCache.evictProduct(savedProduct.getId()));
                            });
                });
    }

    private Mono<Product> handleImageUpdate(Product product, UpdateProductCommand command){
        return command.image()
                .map(img ->
                    fileStorage.deleteImage(product.getImageUrl())
                            .then(fileStorage.storeImage(img))
                            .map(url -> {
                                product.setImageUrl(url);
                                return product;
                            })
        ).orElse(Mono.just(product));
    }

    private CloudEvent<ProductPriceChanged> buildEvent(Product product){
        ProductPriceChanged payload = new ProductPriceChanged(
                product.getId(),
                product.getPrice());

        EventMetadata metadata = new EventMetadata(
                UUID.randomUUID(),
                "ProductPriceChanged",
                "1.0",
                "catalog-service",
                Instant.now());

        return new CloudEvent<>(metadata, payload);
    }
}
