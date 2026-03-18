package com.ecommerce.crtdev.auth_service.controller;

import com.ecommerce.crtdev.auth_service.dto.UserRegisterRequest;
import com.ecommerce.crtdev.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService service;

    public AuthController(UserService service){
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        service.registerUser(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
