package com.ecommerce.crtdev.payment_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_refunds")
public class PaymentRefund {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected PaymentRefund() {}

    public PaymentRefund(String id, String paymentId, String orderId, BigDecimal amount) {
        this.id          = id;
        this.paymentId   = paymentId;
        this.orderId     = orderId;
        this.amount      = amount;
        this.status      = RefundStatus.PENDING;
        this.requestedAt = Instant.now();
    }

    public void markCompleted() {
        this.status      = RefundStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public String getId()          { return id; }
    public String getPaymentId()   { return paymentId; }
    public String getOrderId()     { return orderId; }
    public BigDecimal getAmount()  { return amount; }
    public RefundStatus getStatus() { return status; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
}
