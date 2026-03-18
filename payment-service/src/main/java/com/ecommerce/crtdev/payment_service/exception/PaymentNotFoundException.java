package com.ecommerce.crtdev.payment_service.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String orderId) {
        super("Payment not found for order: " + orderId);
    }
}
