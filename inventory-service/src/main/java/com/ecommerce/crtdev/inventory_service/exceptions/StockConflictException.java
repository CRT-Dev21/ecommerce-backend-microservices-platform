package com.ecommerce.crtdev.inventory_service.exceptions;

public class StockConflictException extends RuntimeException {
    public StockConflictException(String productId) {
        super("Stock reservation conflict after retries for product: " + productId);
    }
}