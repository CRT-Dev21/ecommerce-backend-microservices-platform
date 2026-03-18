package com.ecommerce.crtdev.order_service.controller;

import com.ecommerce.crtdev.order_service.dto.OrderRequests;
import com.ecommerce.crtdev.order_service.dto.OrderResponses;
import com.ecommerce.crtdev.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponses.CheckoutResponse> checkout(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody OrderRequests.CheckoutRequest request) {

        Long userId = Long.parseLong(jwt.getSubject());
        String buyerEmail = jwt.getClaimAsString("email");

        OrderResponses.CheckoutResponse response = orderService.checkout(userId, buyerEmail, request);

        if (response.ordersCreated().isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(response);
        }
        if (response.hasFailures()) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponses.OrderSummaryResponse>> getUserOrders(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponses.OrderSummaryResponse> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> requestRefund(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        orderService.requestRefund(orderId, userId);
        return ResponseEntity.accepted().build();
    }
}
