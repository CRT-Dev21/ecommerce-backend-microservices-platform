package com.ecommerce.crtdev.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "/api/catalog/products/homepage",
            "/oauth2/**",
            "/login",
            "/.well-known/**",
            "/fallback/**",
            "/actuator/health/**"
    };

    @Bean
    public SecurityWebFilterChain filterChain (ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(PUBLIC_PATHS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .build();
    }


}
