package com.ecommerce.crtdev.auth_service.exception.custom;

import com.ecommerce.crtdev.auth_service.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message, String errorCode, HttpStatus status){
        super(message, errorCode, status);
    }
}
