package com.ecommerce.crtdev.order_service.dto;

import java.util.List;

public final class InventoryDtos {

    private InventoryDtos() {}

    public record StockItem(String productId, int quantity) {}
    public record ReserveStockRequest(List<StockItem> items) {}
}