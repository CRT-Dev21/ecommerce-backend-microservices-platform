package com.ecommerce.crtdev.payment_service.dto;

public record BankInfoResponse(
        Long   sellerId,
        String accountHolderName,
        String bankName,
        String country,
        String accountNumber,
        String routingCode
) {}