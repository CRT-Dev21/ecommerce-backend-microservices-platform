package com.ecommerce.crtdev.order_service.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id = Generators.timeBasedEpochGenerator().generate();

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "payment_method_token", nullable = false)
    private String paymentMethodToken;

    @Column(name = "checkout_id", nullable = false)
    private UUID checkoutId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private Long version;

    protected Order() {}

    public Order(Long userId, String buyerEmail, Long sellerId,
                 String paymentMethodToken, UUID checkoutId, List<OrderItem> items) {
        this.userId             = userId;
        this.buyerEmail         = buyerEmail;
        this.sellerId           = sellerId;
        this.paymentMethodToken = paymentMethodToken;
        this.checkoutId         = checkoutId;
        this.status             = OrderStatus.PENDING;
        this.createdAt          = Instant.now();
        this.updatedAt          = Instant.now();
        this.totalAmount        = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        items.forEach(i -> i.setOrder(this));
        this.items = new ArrayList<>(items);
    }

    // ── Status transitions ────────────────────────────────────────────────────

    public void markAwaitingPayment()  { transition(OrderStatus.AWAITING_PAYMENT); }
    public void markPaymentProcessing(){ transition(OrderStatus.PAYMENT_PROCESSING); }
    public void markConfirmed()        { transition(OrderStatus.CONFIRMED); }
    public void markCancelled()        { transition(OrderStatus.CANCELLED); }
    public void markRefunding()        { transition(OrderStatus.REFUNDING); }
    public void markRefunded()         { transition(OrderStatus.REFUNDED); }

    public boolean isCancellable() {
        return status == OrderStatus.PENDING
                || status == OrderStatus.AWAITING_PAYMENT
                || status == OrderStatus.PAYMENT_PROCESSING;
    }

    public boolean isRefundable() {
        return status == OrderStatus.CONFIRMED;
    }

    private void transition(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition order " + id + " from " + status + " to " + target);
        }
        this.status    = target;
        this.updatedAt = Instant.now();
    }

    public UUID getId()                 { return id; }
    public Long getUserId()               { return userId; }
    public String getBuyerEmail()         { return buyerEmail; }
    public Long getSellerId()             { return sellerId; }
    public String getPaymentMethodToken() { return paymentMethodToken; }
    public UUID getCheckoutId()           { return checkoutId; }
    public OrderStatus getStatus()        { return status; }
    public BigDecimal getTotalAmount()    { return totalAmount; }
    public Instant getCreatedAt()         { return createdAt; }
    public Instant getUpdatedAt()         { return updatedAt; }
    public List<OrderItem> getItems()     { return items; }
}