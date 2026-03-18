package com.ecommerce.crtdev.seller_service.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankAccount bankAccount;

    protected Seller() {}

    public Seller(Long userId, String businessName, String email, String phone) {
        this.userId       = userId;
        this.businessName = businessName;
        this.email        = email;
        this.phone        = phone;
        this.createdAt    = Instant.now();
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        bankAccount.setSeller(this);
    }

    public Long getId()             { return id; }
    public Long getUserId()         { return userId; }
    public String getBusinessName() { return businessName; }
    public String getEmail()        { return email; }
    public String getPhone()        { return phone; }
    public Instant getCreatedAt()   { return createdAt; }
    public BankAccount getBankAccount() { return bankAccount; }
}