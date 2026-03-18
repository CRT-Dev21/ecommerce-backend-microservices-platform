package com.ecommerce.crtdev.inventory_service.exceptions;

public class InsufficientStockException extends RuntimeException {
    private final int available;
    public InsufficientStockException(String productId, int available) {
        super("Insufficient stock for product: " + productId);
        this.available = available;
    }
    public int getAvailable() { return available; }
}