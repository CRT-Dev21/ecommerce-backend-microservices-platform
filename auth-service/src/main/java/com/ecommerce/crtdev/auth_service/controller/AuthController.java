package com.ecommerce.crtdev.auth_service.controller;

import com.ecommerce.crtdev.auth_service.dto.UserRegisterRequest;
import com.ecommerce.crtdev.auth_service.entity.User;
import com.ecommerce.crtdev.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService service;

    public AuthController(UserService service){
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        User user = service.registerUser(userRegisterRequest);
        log.info("User registered successfully with ID: {}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
