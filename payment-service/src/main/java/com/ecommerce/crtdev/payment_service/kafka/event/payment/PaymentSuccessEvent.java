package com.ecommerce.crtdev.payment_service.kafka.event.payment;

public record PaymentSuccessEvent(String orderId) {}