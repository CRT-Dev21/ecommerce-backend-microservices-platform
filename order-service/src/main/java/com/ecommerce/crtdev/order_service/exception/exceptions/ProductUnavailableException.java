package com.ecommerce.crtdev.order_service.exception.exceptions;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(String id) {
        super("Product not available: " + id); }
}
