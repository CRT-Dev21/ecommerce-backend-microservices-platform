package com.ecommerce.crtdev.payment_service.dto;

import com.ecommerce.crtdev.payment_service.entity.CardBrand;
import com.ecommerce.crtdev.payment_service.entity.Payment;
import com.ecommerce.crtdev.payment_service.entity.PaymentMethod;
import com.ecommerce.crtdev.payment_service.entity.PaymentRefund;

import java.math.BigDecimal;
import java.time.Instant;

public final class PaymentResponses {

    private PaymentResponses() {}

    public record TokenizeResponse(
            String    token,
            String    last4,
            CardBrand brand,
            String    holderName,
            int       expiryMonth,
            int       expiryYear
    ) {
        public static TokenizeResponse from(PaymentMethod pm) {
            return new TokenizeResponse(
                    pm.getId(), pm.getLast4(), pm.getBrand(),
                    pm.getHolderName(), pm.getExpiryMonth(), pm.getExpiryYear()
            );
        }
    }

    public record PaymentResponse(
            String     id,
            String     orderId,
            BigDecimal amount,
            String     status,
            Instant attemptedAt,
            Instant    completedAt
    ) {
        public static PaymentResponse from(Payment p) {
            return new PaymentResponse(
                    p.getId(), p.getOrderId(), p.getAmount(),
                    p.getStatus().name(), p.getAttemptedAt(), p.getCompletedAt()
            );
        }
    }

    public record RefundResponse(
            String     id,
            String     orderId,
            BigDecimal amount,
            String     status,
            Instant    requestedAt,
            Instant    completedAt
    ) {
        public static RefundResponse from(PaymentRefund r) {
            return new RefundResponse(
                    r.getId(), r.getOrderId(), r.getAmount(),
                    r.getStatus().name(), r.getRequestedAt(), r.getCompletedAt()
            );
        }
    }
}
