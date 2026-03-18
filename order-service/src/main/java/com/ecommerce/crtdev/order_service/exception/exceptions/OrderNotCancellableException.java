package com.ecommerce.crtdev.order_service.exception.exceptions;

import com.ecommerce.crtdev.order_service.entity.OrderStatus;

public class OrderNotCancellableException extends RuntimeException {
    public OrderNotCancellableException(String id, OrderStatus s) {
        super("Order " + id + " cannot be cancelled in status: " + s); }
}