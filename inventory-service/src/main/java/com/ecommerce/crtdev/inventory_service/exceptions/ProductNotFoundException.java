package com.ecommerce.crtdev.inventory_service.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super("Inventory record not found for product: " + productId);
    }
}