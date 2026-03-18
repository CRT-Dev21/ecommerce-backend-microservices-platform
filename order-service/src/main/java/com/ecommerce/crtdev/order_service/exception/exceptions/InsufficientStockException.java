package com.ecommerce.crtdev.order_service.exception.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String msg) { super(msg); }
}