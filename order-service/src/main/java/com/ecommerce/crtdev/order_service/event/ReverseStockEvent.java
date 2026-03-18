package com.ecommerce.crtdev.order_service.event;

import java.util.List;
import java.util.UUID;

public record ReverseStockEvent(
        UUID orderId,
        List<OrderItemInEvent> items
) {}
