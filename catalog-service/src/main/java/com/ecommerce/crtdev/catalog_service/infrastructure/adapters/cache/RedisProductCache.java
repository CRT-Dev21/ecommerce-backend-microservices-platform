package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.cache;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductCache;
import com.ecommerce.crtdev.catalog_service.infrastructure.mappers.ProductEntitiesMapper;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.redis.ProductCacheEntity;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class RedisProductCache implements IProductCache {

    private final ReactiveRedisTemplate<String, ProductCacheEntity> redisTemplate;

    public RedisProductCache(ReactiveRedisTemplate<String, ProductCacheEntity> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public String key(String id){
        return "product:"+id;
    }

    @Override
    public Mono<Product> getProduct(String productId) {
        return redisTemplate.opsForValue()
                .get(key(productId))
                .map(ProductEntitiesMapper::cacheToDomain);
    }

    @Override
    public Mono<Void> putProduct(Product product) {
        ProductCacheEntity entity = ProductEntitiesMapper.domainToCache(product);

        return redisTemplate.opsForValue()
                .set(key(product.getId()), entity, Duration.ofMinutes(10))
                .then();
    }

    @Override
    public Mono<Void> evictProduct(String productId) {
        return redisTemplate.delete(key(productId)).then();
    }
}
