package com.ecommerce.crtdev.payment_service.dto;

import jakarta.validation.constraints.*;

public final class PaymentRequests {

    private PaymentRequests() {}

    public record TokenizeCardRequest(
            @NotBlank @Size(min = 13, max = 19) String cardNumber,
            @NotBlank String holderName,
            @NotNull @Min(1) @Max(12) Integer expiryMonth,
            @NotNull @Min(2024) Integer expiryYear,
            @NotBlank @Size(min = 3, max = 4) String cvv
    ) {}
}