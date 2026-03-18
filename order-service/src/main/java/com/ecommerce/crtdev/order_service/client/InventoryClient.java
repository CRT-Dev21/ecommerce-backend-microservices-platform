package com.ecommerce.crtdev.order_service.client;

import com.ecommerce.crtdev.order_service.dto.InventoryDtos;
import com.ecommerce.crtdev.order_service.exception.exceptions.InsufficientStockException;
import com.ecommerce.crtdev.order_service.exception.exceptions.InventoryConflictException;
import com.ecommerce.crtdev.order_service.exception.exceptions.InventoryProductNotFoundException;
import com.ecommerce.crtdev.order_service.exception.exceptions.InventoryUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);
    private static final String CIRCUIT_BREAKER_NAME = "inventory";
    private static final String RETRY_NAME = "inventory";

    private final WebClient inventoryWebClient;

    public InventoryClient(WebClient inventoryWebClient) {
        this.inventoryWebClient = inventoryWebClient;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "reserveFallback")
    @Retry(name = RETRY_NAME)
    public void reserve(List<InventoryDtos.StockItem> items) {
        try {
            inventoryWebClient.post()
                    .uri("/inventory/reserve")
                    .bodyValue(new InventoryDtos.ReserveStockRequest(items))
                    .retrieve()
                    .onStatus(
                            status -> status == HttpStatus.CONFLICT,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new InsufficientStockException(
                                            extractDetail(body)))
                    )
                    .onStatus(
                            status -> status == HttpStatus.NOT_FOUND,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new InventoryProductNotFoundException(
                                            extractDetail(body)))
                    )
                    .onStatus(
                            status -> status == HttpStatus.SERVICE_UNAVAILABLE,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new InventoryConflictException(
                                            extractDetail(body)))
                    )
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new InventoryUnavailableException(
                                            "Inventory server error: " + extractDetail(body)))
                    )
                    .toBodilessEntity()
                    .block();

        } catch (InsufficientStockException
                 | InventoryProductNotFoundException
                 | InventoryConflictException e) {
            throw e;
        } catch (WebClientResponseException e) {
            log.error("Inventory HTTP error status={} body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new InventoryUnavailableException(
                    "Inventory HTTP error: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Unexpected error calling inventory service", e);
            throw new InventoryUnavailableException(
                    "Could not reach inventory service: " + e.getMessage());
        }
    }

    private void reserveFallback(List<InventoryDtos.StockItem> items, Throwable throwable) {
        if (throwable instanceof InsufficientStockException
                || throwable instanceof InventoryProductNotFoundException
                || throwable instanceof InventoryConflictException) {
            if (throwable instanceof RuntimeException re) throw re;
        }

        log.error("Circuit breaker open for inventory service — fallback triggered. Cause: {}",
                throwable.getMessage());
        throw new InventoryUnavailableException(
                "Inventory service is temporarily unavailable. Please try again later.");
    }

    private String extractDetail(String body) {
        try {
            if (body != null && body.contains("\"detail\"")) {
                int start = body.indexOf("\"detail\"") + 10;
                int end   = body.indexOf("\"", start + 1);
                if (start > 0 && end > start) {
                    return body.substring(start, end);
                }
            }
        } catch (Exception ignored) {}
        return body != null ? body : "No details available";
    }
}