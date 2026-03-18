package com.ecommerce.crtdev.seller_service.exception.custom;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(Long sellerId) {
        super("Bank account not configured for seller: " + sellerId);
    }
}