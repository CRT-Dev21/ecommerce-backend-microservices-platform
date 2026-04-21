package com.ecommerce.crtdev.catalog_service.domain.model;

import java.util.List;

public class Category {
    private Long id;
    private String name;
    private String description;
    private List<Product> products;

    public Category(String description, Long id, String name, List<Product> products) {
        this.description = description;
        this.id = id;
        this.name = name;
        this.products = products;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if (product != null){
            products.add(product);
        }
    }
}
