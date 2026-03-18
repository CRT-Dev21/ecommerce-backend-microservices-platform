package com.ecommerce.crtdev.inventory_service.kafka.events;

import java.util.List;

public record OrderConfirmedEvent(String orderId, List<OrderItem> items) {}