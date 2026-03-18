package com.ecommerce.crtdev.payment_service.kafka.event.order;

import java.math.BigDecimal;

public record PaymentRefundRequestEvent(
        String     orderId,
        Long       userId,
        String     buyerEmail,
        BigDecimal amount
) {}