package com.ecommerce.crtdev.notification_service.event;

public record CloudEvent<T>(EventMetadata metadata, T payload) {}
