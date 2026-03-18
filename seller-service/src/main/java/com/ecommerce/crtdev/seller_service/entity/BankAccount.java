package com.ecommerce.crtdev.seller_service.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private Seller seller;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "routing_code")
    private String routingCode;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BankAccount() {}

    public BankAccount(String accountHolderName, String bankName, String country,
                       String accountNumber, String routingCode) {
        this.accountHolderName = accountHolderName;
        this.bankName          = bankName;
        this.country           = country.toUpperCase();
        this.accountNumber     = accountNumber;
        this.routingCode       = routingCode;
        this.updatedAt         = Instant.now();
    }

    public void update(String accountHolderName, String bankName, String country,
                       String accountNumber, String routingCode) {
        this.accountHolderName = accountHolderName;
        this.bankName          = bankName;
        this.country           = country.toUpperCase();
        this.accountNumber     = accountNumber;
        this.routingCode       = routingCode;
        this.updatedAt         = Instant.now();
    }

    public void setSeller(Seller seller) { this.seller = seller; }

    public Long getId()                  { return id; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getBankName()          { return bankName; }
    public String getCountry()           { return country; }
    public String getAccountNumber()     { return accountNumber; }
    public String getRoutingCode()       { return routingCode; }
    public Instant getUpdatedAt()        { return updatedAt; }
}
