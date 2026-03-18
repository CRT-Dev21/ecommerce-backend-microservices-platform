package com.ecommerce.crtdev.cart_service.exception;

public class CartSerializationException extends RuntimeException {
    public CartSerializationException(String message, Exception e){
        super(message);
    }
}
