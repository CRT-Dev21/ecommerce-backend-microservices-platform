package com.ecommerce.crtdev.inventory_service.service;

import com.ecommerce.crtdev.inventory_service.dto.InventoryRequests;
import com.ecommerce.crtdev.inventory_service.dto.InventoryResponse;
import com.ecommerce.crtdev.inventory_service.dto.ReservationResult;
import com.ecommerce.crtdev.inventory_service.exceptions.ProductNotFoundException;
import com.ecommerce.crtdev.inventory_service.exceptions.UnauthorizedSellerException;
import com.ecommerce.crtdev.inventory_service.kafka.events.OrderItem;
import com.ecommerce.crtdev.inventory_service.model.InventoryItem;
import com.ecommerce.crtdev.inventory_service.repository.InventoryRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BASE_MS = 50L;

    private final InventoryRepository inventoryRepository;
    private final ReservationExecutor reservationExecutor;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ReservationExecutor reservationExecutor) {
        this.inventoryRepository = inventoryRepository;
        this.reservationExecutor = reservationExecutor;
    }

    @Transactional
    public void createInventory(String productId, Long sellerId, int initialStock) {
        if (inventoryRepository.existsById(productId)) return;
        inventoryRepository.save(new InventoryItem(productId, sellerId, initialStock));
    }

    @Transactional
    public void deleteInventory(String productId) {
        inventoryRepository.deleteById(productId);
    }

    public ReservationResult reserveAll(List<InventoryRequests.StockItem> items) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return reservationExecutor.attemptBulkReservation(items);
            } catch (OptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES) {
                    return new ReservationResult.ConflictAfterRetries("bulk");
                }
                sleep(RETRY_BASE_MS * attempt);
            }
        }
        return new ReservationResult.ConflictAfterRetries("bulk");
    }

    @Transactional
    public void reverseAll(List<OrderItem> items) {
        for (OrderItem item : items) {
            inventoryRepository.findById(item.productId()).ifPresent(inventoryItem -> {
                inventoryItem.reverse(item.quantity());
                inventoryRepository.save(inventoryItem);
            });
        }
    }

    @Transactional
    public void confirmAll(List<OrderItem> items) {
        for (OrderItem item : items) {
            inventoryRepository.findById(item.productId()).ifPresent(inventoryItem -> {
                inventoryItem.confirmReservation(item.quantity());
                inventoryRepository.save(inventoryItem);
            });
        }
    }

    @Transactional
    public InventoryResponse updateStock(String productId, Long sellerId, InventoryRequests.UpdateStockRequest request) {
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!item.getSellerId().equals(sellerId)) {
            throw new UnauthorizedSellerException(productId);
        }

        item.updateStock(request.delta());
        return InventoryResponse.from(inventoryRepository.save(item));
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStock(String productId) {
        return inventoryRepository.findById(productId)
                .map(InventoryResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}