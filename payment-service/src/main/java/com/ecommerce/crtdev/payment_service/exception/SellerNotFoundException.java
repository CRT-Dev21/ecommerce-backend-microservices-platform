package com.ecommerce.crtdev.payment_service.exception;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(Long sellerId) {
        super("Seller not found or has no bank account: " + sellerId);
    }
}
