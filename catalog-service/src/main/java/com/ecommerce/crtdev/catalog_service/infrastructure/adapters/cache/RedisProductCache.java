package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.cache;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisProductCache implements IProductCache {

    private final RedisTemplate<String, Product> redisTemplate;
    private static final String PREFIX = "product:";

    public RedisProductCache(RedisTemplate<String, Product> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Product> getProduct(String productId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + productId));
    }

    @Override
    public void putProduct(Product product) {
        redisTemplate.opsForValue().set(PREFIX + product.getId(), product, Duration.ofHours(24));
    }

    @Override
    public void evictProduct(String productId) {
        redisTemplate.delete(PREFIX + productId);
    }
}
