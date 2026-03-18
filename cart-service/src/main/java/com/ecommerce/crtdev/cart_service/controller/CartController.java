package com.ecommerce.crtdev.cart_service.controller;

import com.ecommerce.crtdev.cart_service.dto.CartRequest;
import com.ecommerce.crtdev.cart_service.dto.CartResponse;
import com.ecommerce.crtdev.cart_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Mono<CartResponse.CartDto> getCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.getCart(userId(jwt))
                .map(CartResponse.CartDto::from);
    }

    @PostMapping("/items")
    public Mono<CartResponse.CartItemDto> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartRequest.AddItemRequest request) {
        return cartService.addItem(userId(jwt), request)
                .map(CartResponse.CartItemDto::from);
    }

    @PatchMapping("/items/{productId}")
    public Mono<CartResponse.CartItemDto> updateQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId,
            @Valid @RequestBody CartRequest.UpdateQuantityRequest request) {
        return cartService.updateQuantity(userId(jwt), productId, request)
                .map(CartResponse.CartItemDto::from);
    }

    @DeleteMapping("/items/{productId}")
    public Mono<Void> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId) {
        return cartService.removeItem(userId(jwt), productId);
    }

    @DeleteMapping
    public Mono<Void> clearCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.clearCart(userId(jwt));
    }

    private String userId(Jwt jwt) {
        return jwt.getSubject();
    }
}