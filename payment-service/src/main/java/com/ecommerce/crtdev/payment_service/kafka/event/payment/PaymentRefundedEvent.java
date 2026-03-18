package com.ecommerce.crtdev.payment_service.kafka.event.payment;

public record PaymentRefundedEvent(String orderId) {}