package com.ecommerce.crtdev.order_service.exception.exceptions;

public class InventoryProductNotFoundException extends RuntimeException {
    public InventoryProductNotFoundException(String msg) { super(msg); }
}
