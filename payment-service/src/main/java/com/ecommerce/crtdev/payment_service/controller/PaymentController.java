package com.ecommerce.crtdev.payment_service.controller;

import com.ecommerce.crtdev.payment_service.dto.PaymentRequests;
import com.ecommerce.crtdev.payment_service.dto.PaymentResponses;
import com.ecommerce.crtdev.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/tokenize")
    public ResponseEntity<PaymentResponses.TokenizeResponse> tokenize(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PaymentRequests.TokenizeCardRequest request) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.tokenize(userId, request));
    }
}

