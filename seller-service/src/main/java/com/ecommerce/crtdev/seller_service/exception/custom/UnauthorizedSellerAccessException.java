package com.ecommerce.crtdev.seller_service.exception.custom;

public class UnauthorizedSellerAccessException extends RuntimeException {
    public UnauthorizedSellerAccessException() {
        super("You are not authorized to access this seller profile");
    }
}