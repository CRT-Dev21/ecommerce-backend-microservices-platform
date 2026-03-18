package com.ecommerce.crtdev.inventory_service.dto;

import com.ecommerce.crtdev.inventory_service.model.InventoryItem;

public record InventoryResponse(
        String productId,
        int availableStock
) {
    public static InventoryResponse from(InventoryItem item) {
        return new InventoryResponse(
                item.getProductId(),
                item.getAvailableStock()
        );
    }
}