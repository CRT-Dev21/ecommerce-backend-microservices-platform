package com.ecommerce.crtdev.seller_service.dto;

import com.ecommerce.crtdev.seller_service.entity.BankAccount;
import com.ecommerce.crtdev.seller_service.entity.Seller;

import java.time.Instant;

public final class SellerResponses {

    private SellerResponses() {}

    public record SellerProfileResponse(
            Long    id,
            Long    userId,
            String  businessName,
            String  email,
            String  phone,
            Instant createdAt,
            BankAccountSummaryResponse bankAccount
    ) {
        public static SellerProfileResponse from(Seller seller) {
            return new SellerProfileResponse(
                    seller.getId(),
                    seller.getUserId(),
                    seller.getBusinessName(),
                    seller.getEmail(),
                    seller.getPhone(),
                    seller.getCreatedAt(),
                    seller.getBankAccount() != null
                            ? BankAccountSummaryResponse.from(seller.getBankAccount())
                            : null
            );
        }
    }

    public record SellerInfoResponse(
            Long    id,
            String  businessName,
            String  email,
            Instant createdAt
    ) {
        public static SellerInfoResponse from(Seller seller) {
            return new SellerInfoResponse(
                    seller.getId(),
                    seller.getBusinessName(),
                    seller.getEmail(),
                    seller.getCreatedAt()
            );
        }
    }

    public record BankAccountSummaryResponse(
            String accountHolderName,
            String bankName,
            String country,
            String maskedAccount
    ) {
        public static BankAccountSummaryResponse from(BankAccount account) {
            String number = account.getAccountNumber();
            String masked = "****" + number.substring(Math.max(0, number.length() - 4));
            return new BankAccountSummaryResponse(
                    account.getAccountHolderName(),
                    account.getBankName(),
                    account.getCountry(),
                    masked
            );
        }
    }

    public record BankInfoResponse(
            Long   sellerId,
            String accountHolderName,
            String bankName,
            String country,
            String accountNumber,
            String routingCode
    ) {
        public static BankInfoResponse from(Long sellerId, BankAccount account) {
            return new BankInfoResponse(
                    sellerId,
                    account.getAccountHolderName(),
                    account.getBankName(),
                    account.getCountry(),
                    account.getAccountNumber(),
                    account.getRoutingCode()
            );
        }
    }
}