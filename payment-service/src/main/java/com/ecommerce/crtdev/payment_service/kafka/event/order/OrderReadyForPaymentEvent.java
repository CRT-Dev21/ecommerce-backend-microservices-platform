package com.ecommerce.crtdev.payment_service.kafka.event.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderReadyForPaymentEvent(
        String         orderId,
        Long           userId,
        String         buyerEmail,
        Long           sellerId,
        List<OrderItem> items,
        BigDecimal totalAmount,
        String         paymentMethodToken
) {}

