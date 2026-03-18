package com.ecommerce.crtdev.payment_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "payment_method_token", nullable = false)
    private String paymentMethodToken;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected Payment() {}

    public Payment(String id, String orderId, Long userId, Long sellerId,
                   String paymentMethodToken, BigDecimal amount) {
        this.id                 = id;
        this.orderId            = orderId;
        this.userId             = userId;
        this.sellerId           = sellerId;
        this.paymentMethodToken = paymentMethodToken;
        this.amount             = amount;
        this.status             = PaymentStatus.PENDING;
        this.attemptedAt        = Instant.now();
    }

    public void markSuccess() {
        this.status      = PaymentStatus.SUCCESS;
        this.completedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status        = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.completedAt   = Instant.now();
    }

    public void markRefunded() {
        this.status      = PaymentStatus.REFUNDED;
        this.completedAt = Instant.now();
    }

    public String getId()                  { return id; }
    public String getOrderId()             { return orderId; }
    public Long getUserId()                { return userId; }
    public Long getSellerId()              { return sellerId; }
    public String getPaymentMethodToken()  { return paymentMethodToken; }
    public BigDecimal getAmount()          { return amount; }
    public PaymentStatus getStatus()       { return status; }
    public String getFailureReason()       { return failureReason; }
    public Instant getAttemptedAt()        { return attemptedAt; }
    public Instant getCompletedAt()        { return completedAt; }
}
