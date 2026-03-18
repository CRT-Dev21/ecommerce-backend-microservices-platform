package com.ecommerce.crtdev.inventory_service.service;

import com.ecommerce.crtdev.inventory_service.dto.InventoryRequests;
import com.ecommerce.crtdev.inventory_service.dto.ReservationResult;
import com.ecommerce.crtdev.inventory_service.model.InventoryItem;
import com.ecommerce.crtdev.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReservationExecutor {

    private final InventoryRepository inventoryRepository;

    public ReservationExecutor(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public ReservationResult attemptBulkReservation(List<InventoryRequests.StockItem> items) {
        for (InventoryRequests.StockItem item : items) {
            InventoryItem inventoryItem = inventoryRepository
                    .findByProductIdForUpdate(item.productId())
                    .orElse(null);

            if (inventoryItem == null) {
                return new ReservationResult.ProductNotFound(item.productId());
            }
            if (inventoryItem.getAvailableStock() < item.quantity()) {
                return new ReservationResult.InsufficientStock(
                        item.productId(), inventoryItem.getAvailableStock());
            }

            inventoryItem.reserve(item.quantity());
            inventoryRepository.save(inventoryItem);
        }

        List<String> reservedIds = items.stream()
                .map(InventoryRequests.StockItem::productId)
                .toList();

        return new ReservationResult.Success(reservedIds);
    }
}
