package com.ecommerce.crtdev.cart_service.repository;

import com.ecommerce.crtdev.cart_service.exception.CartSerializationException;
import com.ecommerce.crtdev.cart_service.model.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Repository
public class CartRepository {

    private static final String KEY_PREFIX = "cart:";
    private static final Duration TTL = Duration.ofDays(14);

    private final ReactiveHashOperations<String, String, String> hashOps;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CartRepository(
            ReactiveRedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    public Flux<CartItem> findAllItems(String userId) {
        return hashOps.values(key(userId))
                .flatMap(this::deserialize);
    }

    public Mono<CartItem> findItem(String userId, String productId) {
        return hashOps.get(key(userId), productId)
                .flatMap(this::deserialize);
    }

    public Mono<Boolean> exists(String userId) {
        return redisTemplate.hasKey(key(userId));
    }

    public Mono<CartItem> saveItem(String userId, CartItem item) {
        return serialize(item)
                .flatMap(json -> hashOps.put(key(userId), item.productId(), json))
                .then(refreshTtl(userId))
                .thenReturn(item);
    }

    public Mono<Void> removeItem(String userId, String productId) {
        return hashOps.remove(key(userId), (Object) productId)
                .then(refreshTtl(userId))
                .then();
    }

    public Mono<Void> clearCart(String userId) {
        return redisTemplate.delete(key(userId)).then();
    }

    private String key(String userId) {
        return KEY_PREFIX + userId;
    }

    private Mono<Void> refreshTtl(String userId) {
        return redisTemplate.expire(key(userId), TTL).then();
    }

    private Mono<String> serialize(CartItem item) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(item))
                .onErrorMap(JsonProcessingException.class,
                        e -> new CartSerializationException("Failed to serialize CartItem", e));
    }

    private Mono<CartItem> deserialize(String json) {
        return Mono.fromCallable(() -> objectMapper.readValue(json, CartItem.class))
                .onErrorMap(JsonProcessingException.class,
                        e -> new CartSerializationException("Failed to deserialize CartItem", e));
    }
}