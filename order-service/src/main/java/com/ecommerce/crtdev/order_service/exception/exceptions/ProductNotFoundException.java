package com.ecommerce.crtdev.order_service.exception.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String msg) { super(msg); }
}