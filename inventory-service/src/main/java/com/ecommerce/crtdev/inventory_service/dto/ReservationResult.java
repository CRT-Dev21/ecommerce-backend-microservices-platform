package com.ecommerce.crtdev.inventory_service.dto;

import java.util.List;

public sealed interface ReservationResult permits
        ReservationResult.Success,
        ReservationResult.InsufficientStock,
        ReservationResult.ConflictAfterRetries,
        ReservationResult.ProductNotFound {

    record Success(List<String> reservedProductIds)
            implements ReservationResult {}

    record InsufficientStock(String productId, int available)
            implements ReservationResult {}

    record ConflictAfterRetries(String productId)
            implements ReservationResult {}

    record ProductNotFound(String productId)
            implements ReservationResult {}
}