package com.ecommerce.crtdev.order_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundRequestEvent(
        UUID orderId,
        Long userId,
        String buyerEmail,
        BigDecimal amount
) {}