package com.ecommerce.crtdev.auth_service.exception.custom;

import com.ecommerce.crtdev.auth_service.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String message, String errorCode, HttpStatus status){
        super(message, errorCode, status);
    }
}
