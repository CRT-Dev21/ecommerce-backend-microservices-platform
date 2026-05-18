package com.ecommerce.crtdev.catalog_service.application.handlers;

import com.ecommerce.crtdev.catalog_service.application.commands.UpdateProductCommand;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import com.ecommerce.crtdev.catalog_service.domain.events.EventMetadata;
import com.ecommerce.crtdev.catalog_service.domain.events.produces.ProductPriceChanged;
import com.ecommerce.crtdev.catalog_service.domain.exception.ProductNotFoundException;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import org.springframework.transaction.annotation.Transactional;

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

    public ProductResponse execute(UpdateProductCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new ProductNotFoundException(command.productId()));

        boolean priceChanged = command.price()
                .filter(newPrice -> !newPrice.equals(product.getPrice()))
                .isPresent();

        String oldImageUrl = product.getImageUrl();
        String newImageUrl = null;

        if (command.image().isPresent()) {
            newImageUrl = fileStorage.storeImage(command.image().get());
            product.setImageUrl(newImageUrl);
        }

        command.name().ifPresent(product::setName);
        command.description().ifPresent(product::setDescription);
        command.price().ifPresent(product::setPrice);
        command.stock().ifPresent(product::setStock);

        Product savedProduct;
        try {
            savedProduct = productRepository.save(product);
        } catch (Exception e) {
            if (newImageUrl != null) fileStorage.deleteImage(newImageUrl);
            throw e;
        }

        productCache.evictProduct(savedProduct.getId());

        if (priceChanged) {
            eventPublisher.publishEvent(savedProduct.getId(), buildEvent(savedProduct));
        }

        if (newImageUrl != null) {
            fileStorage.deleteImage(oldImageUrl);
        }

        return new ProductResponse(savedProduct.getId(), savedProduct.getName(),
                savedProduct.getDescription(), savedProduct.getPrice(),
                savedProduct.getImageUrl());
    }

    private void handleImageUpdate(Product product, UpdateProductCommand command) {
        command.image().ifPresent(img -> {
            String newImageUrl = fileStorage.storeImage(img);
            product.setImageUrl(newImageUrl);
        });
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
