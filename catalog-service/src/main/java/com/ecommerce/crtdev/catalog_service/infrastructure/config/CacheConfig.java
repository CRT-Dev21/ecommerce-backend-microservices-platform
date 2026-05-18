package com.ecommerce.crtdev.catalog_service.infrastructure.config;

import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    private final RedisConnectionFactory connectionFactory;

    public CacheConfig(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public RedisCacheManager cacheManager() {

        var listSerializer = new JacksonJsonRedisSerializer<List<ProductResponse>>(
                new tools.jackson.databind.ObjectMapper()
                        .getTypeFactory()
                        .constructCollectionType(List.class, ProductResponse.class)
        );

        RedisCacheConfiguration listCacheConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(listSerializer)
                )
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("homepage-products",
                        listCacheConfig.entryTtl(Duration.ofMinutes(2)))
                .withCacheConfiguration("product-search",
                        listCacheConfig.entryTtl(Duration.ofSeconds(30)))
                .build();
    }
}