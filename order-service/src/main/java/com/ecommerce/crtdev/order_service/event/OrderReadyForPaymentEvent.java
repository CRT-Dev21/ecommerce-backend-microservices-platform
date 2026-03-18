package com.ecommerce.crtdev.order_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderReadyForPaymentEvent(
        UUID orderId,
        Long userId,
        Long sellerId,
        BigDecimal totalAmount,
        String paymentMethodToken
) {}
