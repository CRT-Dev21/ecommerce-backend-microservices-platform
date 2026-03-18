package com.ecommerce.crtdev.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public final class OrderRequests {

    private OrderRequests() {}

    public record CheckoutRequest(
            @NotEmpty @Valid List<CheckoutItemRequest> items,
            @NotBlank String paymentMethodToken
    ) {}

    public record CheckoutItemRequest(
            @NotBlank String productId,
            @NotNull @Min(1) Integer quantity,
            @NotNull @DecimalMin("0.01") BigDecimal price
    ) {}
}