package com.ecommerce.crtdev.cart_service.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String id){
        super(id);
    }
}
