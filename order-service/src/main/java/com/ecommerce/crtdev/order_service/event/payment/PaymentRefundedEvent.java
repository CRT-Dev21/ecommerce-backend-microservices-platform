package com.ecommerce.crtdev.order_service.event.payment;

import java.util.UUID;

public record PaymentRefundedEvent(UUID orderId) {}