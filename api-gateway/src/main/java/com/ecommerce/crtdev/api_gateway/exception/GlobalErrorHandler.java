package com.ecommerce.crtdev.api_gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@Order(-1)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String errorCode;
        String message;

        if (ex instanceof OAuth2AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = "INVALID_TOKEN";
            message = "Invalid token or expired";
            log.warn(
                    "Auth failure path={} error={}",
                    exchange.getRequest().getPath(),
                    ex.getMessage()
            );
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            errorCode = "REQUEST_ERROR";
            message = rse.getReason() != null ? rse.getReason() : "Error in request";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = "GATEWAY_ERROR";
            message = "Gateway internal error";
            log.error(
                    "Unhandled error path={}",
                    exchange.getRequest().getPath(),
                    ex
            );
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(Map.of(
                    "error",   errorCode,
                    "message", message,
                    "path",    exchange.getRequest().getPath().value()
            ));
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Failed to write error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}
