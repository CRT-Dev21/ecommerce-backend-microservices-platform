package com.ecommerce.crtdev.order_service.event;

import java.util.List;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId,
        String buyerEmail,
        List<OrderItemInEvent> items
) {}