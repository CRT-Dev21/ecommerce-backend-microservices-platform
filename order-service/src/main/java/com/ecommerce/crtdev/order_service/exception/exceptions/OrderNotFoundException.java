package com.ecommerce.crtdev.order_service.exception.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) { super("Order not found: " + id); }
}
