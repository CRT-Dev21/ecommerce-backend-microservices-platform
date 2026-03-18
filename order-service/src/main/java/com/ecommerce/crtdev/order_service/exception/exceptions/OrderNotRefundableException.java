package com.ecommerce.crtdev.order_service.exception.exceptions;

import com.ecommerce.crtdev.order_service.entity.OrderStatus;

public class OrderNotRefundableException extends RuntimeException {
    public OrderNotRefundableException(String id, OrderStatus s) {
        super("Order " + id + " cannot be refunded in status: " + s); }
}