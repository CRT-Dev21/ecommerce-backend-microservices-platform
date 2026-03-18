package com.ecommerce.crtdev.order_service.event;

import java.util.UUID;

public record OrderCancelledEvent(
        UUID orderId,
        String buyerEmail,
        String reason
) {}