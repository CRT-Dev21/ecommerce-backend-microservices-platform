package com.ecommerce.crtdev.payment_service.entity;

import jakarta.persistence.*;
import java.time.Instant;

// Método de pago tokenizado — nunca almacenamos datos reales de tarjeta
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @Column(length = 36)
    private String id;          // UUID generado por nosotros = el token opaco

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last4", nullable = false, length = 4)
    private String last4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CardBrand brand;

    @Column(name = "holder_name", nullable = false)
    private String holderName;

    @Column(name = "expiry_month", nullable = false)
    private int expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private int expiryYear;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PaymentMethod() {}

    public PaymentMethod(String id, Long userId, String last4, CardBrand brand,
                         String holderName, int expiryMonth, int expiryYear) {
        this.id          = id;
        this.userId      = userId;
        this.last4       = last4;
        this.brand       = brand;
        this.holderName  = holderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear  = expiryYear;
        this.createdAt   = Instant.now();
    }

    public String getId()          { return id; }
    public Long getUserId()        { return userId; }
    public String getLast4()       { return last4; }
    public CardBrand getBrand()    { return brand; }
    public String getHolderName()  { return holderName; }
    public int getExpiryMonth()    { return expiryMonth; }
    public int getExpiryYear()     { return expiryYear; }
    public Instant getCreatedAt()  { return createdAt; }
}

