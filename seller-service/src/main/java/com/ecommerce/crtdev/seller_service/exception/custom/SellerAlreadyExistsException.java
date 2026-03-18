package com.ecommerce.crtdev.seller_service.exception.custom;

public class SellerAlreadyExistsException extends RuntimeException {
    public SellerAlreadyExistsException(Long userId) {
        super("Seller profile already exists for user: " + userId);
    }
}