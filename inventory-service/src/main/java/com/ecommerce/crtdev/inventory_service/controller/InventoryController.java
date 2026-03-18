package com.ecommerce.crtdev.inventory_service.controller;

import com.ecommerce.crtdev.inventory_service.dto.InventoryRequests.ReserveStockRequest;
import com.ecommerce.crtdev.inventory_service.dto.InventoryRequests.UpdateStockRequest;
import com.ecommerce.crtdev.inventory_service.dto.InventoryResponse;
import com.ecommerce.crtdev.inventory_service.dto.ReservationResult;
import com.ecommerce.crtdev.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasAuthority('SCOPE_inventory:reserve')")
    public ResponseEntity<?> reserve(@Valid @RequestBody ReserveStockRequest request) {

        ReservationResult result = inventoryService.reserveAll(request.items());

        if (result instanceof ReservationResult.Success success) {
            return ResponseEntity.ok(success);
        }

        if (result instanceof ReservationResult.InsufficientStock insufficient) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "Insufficient stock for product: " + insufficient.productId()
                            + ". Available: " + insufficient.available());
            problem.setType(URI.create("inventory/insufficient-stock"));
            problem.setProperty("productId", insufficient.productId());
            problem.setProperty("available", insufficient.available());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
        }

        if (result instanceof ReservationResult.ProductNotFound notFound) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "Product not found in inventory: " + notFound.productId());
            problem.setType(URI.create("inventory/product-not-found"));
            problem.setProperty("productId", notFound.productId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
        }

        if (result instanceof ReservationResult.ConflictAfterRetries) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "High demand detected. Please retry in a moment.");
            problem.setType(URI.create("inventory/conflict"));
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
        }

        return ResponseEntity.internalServerError().build();
    }

    @PatchMapping("/{productId}/stock")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable String productId,
            @Valid @RequestBody UpdateStockRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long sellerId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(inventoryService.updateStock(productId, sellerId, request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getStock(productId));
    }
}