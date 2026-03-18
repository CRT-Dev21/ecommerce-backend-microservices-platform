package com.ecommerce.crtdev.notification_service.event;

import java.util.List;

public record OrderConfirmedEvent(
        String         orderId,
        String         buyerEmail,
        List<OrderItem> items
) {}
