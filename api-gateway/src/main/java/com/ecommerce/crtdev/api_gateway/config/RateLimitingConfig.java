package com.ecommerce.crtdev.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitingConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .filter(p -> p instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(token -> "user:" + token.getToken().getSubject())
                .switchIfEmpty(ipKeyResolver().resolve(exchange));
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (ip != null && !ip.isBlank()) {
                ip = ip.split(",")[0].trim();
            } else {
                ip = exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown";
            }
            return Mono.just("ip:" + ip);
        };
    }
}
