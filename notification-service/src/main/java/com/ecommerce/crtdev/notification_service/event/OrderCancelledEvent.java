package com.ecommerce.crtdev.notification_service.event;

public record OrderCancelledEvent(
        String orderId,
        String buyerEmail,
        String reason
) {}
