package com.ecommerce.crtdev.seller_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SellerRequests {

    private SellerRequests() {}

    public record BankAccountRequest(
            @NotBlank String accountHolderName,
            @NotBlank String bankName,
            @NotBlank @Size(min = 2, max = 2) String country,
            @NotBlank String accountNumber,
            String routingCode
    ) {}

    public record RegisterSellerRequest(
            @NotBlank String businessName,
            @NotBlank @Email String email,
            @NotBlank String phone,
            @NotNull @Valid BankAccountRequest bankAccount
    ) {}

    public record UpdateBankAccountRequest(
            @NotBlank String accountHolderName,
            @NotBlank String bankName,
            @NotBlank @Size(min = 2, max = 2) String country,
            @NotBlank String accountNumber,
            String routingCode
    ) {}
}