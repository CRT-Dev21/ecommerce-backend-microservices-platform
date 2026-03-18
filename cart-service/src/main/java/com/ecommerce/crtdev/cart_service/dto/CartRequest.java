package com.ecommerce.crtdev.cart_service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public sealed interface CartRequest {

    record AddItemRequest(
            @NotBlank String productId,
            @NotBlank String name,
            @NotNull @DecimalMin("0.01") BigDecimal price,
            String imageUrl,
            @Min(1) @Max(99) int quantity
    ) implements CartRequest {}

    record UpdateQuantityRequest(
            @Min(1) @Max(99) int quantity
    ) implements CartRequest {}
}