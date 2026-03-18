package com.ecommerce.crtdev.payment_service.kafka;

public record CloudEvent<T>(EventMetadata metadata, T payload) {}
