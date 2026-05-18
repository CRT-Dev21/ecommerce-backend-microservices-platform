package com.ecommerce.crtdev.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/catalog")
    public Mono<ResponseEntity<Map<String, Object>>> catalogFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error",   "SERVICE_UNAVAILABLE",
                "message", "The catalog is temporarily unavailable. Please try again later.",
                "service", "catalog-service"
        )));
    }

    @RequestMapping("/cart")
    public Mono<ResponseEntity<Map<String, Object>>> cartFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error",   "SERVICE_UNAVAILABLE",
                "message", "The cart service is temporarily unavailable.",
                "service", "cart-service"
        )));
    }

    @RequestMapping("/orders")
    public Mono<ResponseEntity<Map<String, Object>>> ordersFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error",   "SERVICE_UNAVAILABLE",
                "message", "The order service is unavailable. Try again later.",
                "service", "order-service"
        )));
    }

    @RequestMapping("/payments")
    public Mono<ResponseEntity<Map<String, Object>>> paymentsFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error",   "SERVICE_UNAVAILABLE",
                "message", "The payment service is unavailable. No payment was processed.",
                "service", "payment-service"
        )));
    }

    @RequestMapping("/generic")
    public Mono<ResponseEntity<Map<String, Object>>> genericFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error",      "SERVICE_UNAVAILABLE",
                "message",    "The service is temporarily unavailable.",
                "retryAfter", 30
        )));
    }
}
