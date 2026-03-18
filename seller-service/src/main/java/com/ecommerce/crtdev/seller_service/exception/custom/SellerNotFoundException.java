package com.ecommerce.crtdev.seller_service.exception.custom;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(Long id) {
        super("Seller not found: " + id);
    }
}
