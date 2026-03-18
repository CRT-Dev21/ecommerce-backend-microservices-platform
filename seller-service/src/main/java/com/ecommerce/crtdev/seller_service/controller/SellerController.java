package com.ecommerce.crtdev.seller_service.controller;

import com.ecommerce.crtdev.seller_service.dto.SellerRequests;
import com.ecommerce.crtdev.seller_service.dto.SellerResponses;
import com.ecommerce.crtdev.seller_service.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SellerResponses.SellerProfileResponse> register(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerRequests.RegisterSellerRequest request) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.register(userId, request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerResponses.SellerProfileResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(sellerService.getMyProfile(userId));
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerResponses.SellerInfoResponse> getSellerInfo(
            @PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerService.getSellerInfo(sellerId));
    }

    @PutMapping("/me/bank-account")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<SellerResponses.BankAccountSummaryResponse> updateBankAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SellerRequests.UpdateBankAccountRequest request) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(sellerService.updateBankAccount(userId, request));
    }

    @GetMapping("/{sellerId}/bank-info")
    @PreAuthorize("hasAuthority('SCOPE_seller:bankInfo')")
    public ResponseEntity<SellerResponses.BankInfoResponse> getBankInfo(
            @PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerService.getBankInfo(sellerId));
    }
}