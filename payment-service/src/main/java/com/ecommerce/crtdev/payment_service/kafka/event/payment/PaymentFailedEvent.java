package com.ecommerce.crtdev.payment_service.kafka.event.payment;

public record PaymentFailedEvent(String orderId, String reason) {}
