package com.ecommerce.crtdev.inventory_service.exceptions;

public class UnauthorizedSellerException extends RuntimeException {
    public UnauthorizedSellerException(String productId) {
        super("Seller not authorized to modify stock for product: " + productId);
    }
}