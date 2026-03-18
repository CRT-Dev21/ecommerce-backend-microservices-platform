package com.ecommerce.crtdev.catalog_service.domain.model;

public class Product {
    private String id;
    private Long sellerId;
    private String name;
    private String description;
    private double price;
    private String categoryId;
    private int stock;
    private String imageUrl;

    public Product(String id, Long sellerId, String name, String description, double price, int stock, String categoryId, String imageUrl) {
        this.id = id;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.description = description;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public Product(Long sellerId, String name, String description, double price, int stock, String categoryId, String imageUrl) {
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.description = description;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setStock(int stock) {
        if (stock <0){
            throw new IllegalStateException("Stock cannot be lower than 0");
        }
        this.stock = stock;
    }
}