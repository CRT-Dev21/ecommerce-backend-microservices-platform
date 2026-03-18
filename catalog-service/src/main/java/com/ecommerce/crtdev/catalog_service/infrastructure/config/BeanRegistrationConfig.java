package com.ecommerce.crtdev.catalog_service.infrastructure.config;

import com.ecommerce.crtdev.catalog_service.application.handlers.*;
import com.ecommerce.crtdev.catalog_service.domain.ports.messaging.IEventPublisher;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanRegistrationConfig {

    @Bean
    public CreateProductHandler createProductHandler(IProductRepository productRepository, IFileStorage fileStorage, IEventPublisher eventPublisher){
        return new CreateProductHandler(productRepository, fileStorage, eventPublisher);
    }

    @Bean
    public DeleteProductHandler deleteProductHandler(IProductRepository productRepository, IProductCache productCache, IEventPublisher eventPublisher){
        return new DeleteProductHandler(productRepository, productCache, eventPublisher);
    }

    @Bean
    public GetHomepageProductsHandler getHomepageProductsHandler(IProductRepository productRepository){
        return new GetHomepageProductsHandler(productRepository);
    }

    @Bean
    public GetProductHandler getProductHandler(IProductRepository productRepository, IProductCache productCache){
        return new GetProductHandler(productRepository, productCache);
    }

    @Bean
    public SearchProductsHandler searchProductsHandler(IProductRepository productRepository){
        return new SearchProductsHandler(productRepository);
    }

    @Bean
    public UpdateProductHandler updateProductHandler(IProductRepository productRepository, IFileStorage fileStorage, IProductCache productCache, IEventPublisher eventPublisher){
        return new UpdateProductHandler(productRepository, fileStorage, productCache, eventPublisher);
    }
}
