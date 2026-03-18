package com.ecommerce.crtdev.catalog_service.infrastructure.config;

import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.redis.ProductCacheEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, ProductCacheEntity> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        RedisSerializationContext<String, ProductCacheEntity> context =
                RedisSerializationContext
                        .<String, ProductCacheEntity>newSerializationContext(new StringRedisSerializer())
                        .value(new Jackson2JsonRedisSerializer<>(ProductCacheEntity.class))
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
