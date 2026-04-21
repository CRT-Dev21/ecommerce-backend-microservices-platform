package com.ecommerce.crtdev.catalog_service.infrastructure.mappers;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.mongo.ProductDocument;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.redis.ProductCacheEntity;

public class ProductEntitiesMapper {

    private ProductEntitiesMapper(){}

    public static ProductDocument domainToDocument (Product product) {

        ProductDocument doc = new ProductDocument();

        doc.setId(product.getId());
        doc.setSellerId(product.getSellerId());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setPrice(product.getPrice());
        doc.setCategoryId(product.getCategoryId());
        doc.setStock(product.getStock());
        doc.setImageUrl(product.getImageUrl());

        return doc;
    }

    public static Product documentToDomain(ProductDocument doc) {

        return new Product(
                doc.getId(),
                doc.getSellerId(),
                doc.getName(),
                doc.getDescription(),
                doc.getPrice(),
                doc.getStock(),
                doc.getCategoryId(),
                doc.getImageUrl()
        );
    }

    public static ProductCacheEntity domainToCache(Product product){

        ProductCacheEntity cache = new ProductCacheEntity();

        cache.setId(product.getId());
        cache.setSellerId(product.getSellerId());
        cache.setName(product.getName());
        cache.setDescription(product.getDescription());
        cache.setPrice(product.getPrice());
        cache.setCategoryId(product.getCategoryId());
        cache.setStock(product.getStock());
        cache.setImageUrl(product.getImageUrl());

        return cache;
    }

    public static Product cacheToDomain(ProductCacheEntity cache){

        return new Product(
                cache.getId(),
                cache.getSellerId(),
                cache.getName(),
                cache.getDescription(),
                cache.getPrice(),
                cache.getStock(),
                cache.getCategoryId(),
                cache.getImageUrl()
        );
    }
}