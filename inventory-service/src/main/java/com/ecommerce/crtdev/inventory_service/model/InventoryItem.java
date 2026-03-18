package com.ecommerce.crtdev.inventory_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class InventoryItem {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "available_stock", nullable = false)
    private int availableStock;

    @Column(name = "reserved_stock", nullable = false)
    private int reservedStock;

    @Version
    private Long version;

    protected InventoryItem(){}

    public InventoryItem(String productId, Long sellerId, int initialStock){
        this.productId = productId;
        this.sellerId = sellerId;
        this.availableStock = initialStock;
        this.reservedStock = 0;
    }

    public void reserve(int quantity){
        if(availableStock<quantity){
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.availableStock -= quantity;
        this.reservedStock += quantity;
    }

    public void reverse(int quantity){
        this.reservedStock = Math.max(0, this.reservedStock-quantity);
        this.availableStock += quantity;
    }

    public void confirmReservation(int quantity){
        this.reservedStock = Math.max(0, this.reservedStock-quantity);
    }

    public void updateStock(int delta){
        int newStock = this.availableStock + delta;

        if(newStock < 0){
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        this.availableStock = newStock;
    }

    public String getProductId()    { return productId; }
    public Long getSellerId()       { return sellerId; }
    public int getAvailableStock()  { return availableStock; }
    public int getReservedStock()   { return reservedStock; }
    public Long getVersion()        { return version; }
}
