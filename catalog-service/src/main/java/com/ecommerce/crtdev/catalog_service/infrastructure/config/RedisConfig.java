package com.ecommerce.crtdev.catalog_service.infrastructure.config;

import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Product> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Product> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        var serializer = new JacksonJsonRedisSerializer<>(Product.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}
