package com.ecommerce.crtdev.inventory_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public final class InventoryRequests {

    private InventoryRequests() {}

    public record StockItem(
            @NotBlank String productId,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record ReserveStockRequest(
            @NotNull @NotEmpty @Valid List<StockItem> items
    ) {}

    public record UpdateStockRequest(
            @NotNull Integer delta
    ) {}
}
