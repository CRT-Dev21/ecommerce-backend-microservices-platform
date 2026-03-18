package com.ecommerce.crtdev.order_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product_catalog_snapshot")
public class ProductSnapshot {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProductSnapshot() {}

    public ProductSnapshot(String productId, Long sellerId,
                           String productName, BigDecimal price) {
        this.productId   = productId;
        this.sellerId    = sellerId;
        this.productName = productName;
        this.price       = price;
        this.updatedAt   = Instant.now();
    }

    public void updatePrice(BigDecimal newPrice) {
        this.price     = newPrice;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active    = false;
        this.updatedAt = Instant.now();
    }

    public String getProductId()    { return productId; }
    public Long getSellerId()       { return sellerId; }
    public String getProductName()  { return productName; }
    public BigDecimal getPrice()    { return price; }
    public boolean isActive()       { return active; }
}

