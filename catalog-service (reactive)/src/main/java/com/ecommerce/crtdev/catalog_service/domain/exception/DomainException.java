package com.ecommerce.crtdev.catalog_service.domain.exception;

public class DomainException extends RuntimeException {
    private final String errorCode;
    public DomainException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}