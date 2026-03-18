package com.ecommerce.crtdev.order_service.exception.exceptions;

public class InventoryConflictException extends RuntimeException {
    public InventoryConflictException(String msg) { super(msg); }
}
