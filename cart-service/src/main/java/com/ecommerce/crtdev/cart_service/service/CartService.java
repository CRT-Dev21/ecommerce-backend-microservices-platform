package com.ecommerce.crtdev.cart_service.service;

import com.ecommerce.crtdev.cart_service.dto.CartRequest;
import com.ecommerce.crtdev.cart_service.exception.CartItemNotFoundException;
import com.ecommerce.crtdev.cart_service.model.Cart;
import com.ecommerce.crtdev.cart_service.model.CartItem;
import com.ecommerce.crtdev.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> getCart(String userId) {
        return cartRepository.findAllItems(userId)
                .collectList()
                .map(items -> new Cart(userId, items));
    }

    public Mono<CartItem> addItem(String userId, CartRequest.AddItemRequest request) {
        return cartRepository.findItem(userId, request.productId())
                .map(existing -> existing.withQuantity(
                        Math.min(existing.quantity() + request.quantity(), 99))
                )
                .defaultIfEmpty(new CartItem(
                        request.productId(),
                        request.name(),
                        request.price(),
                        request.imageUrl(),
                        request.quantity()
                ))
                .flatMap(item -> cartRepository.saveItem(userId, item));
    }

    public Mono<CartItem> updateQuantity(String userId, String productId, CartRequest.UpdateQuantityRequest request) {
        return cartRepository.findItem(userId, productId)
                .switchIfEmpty(Mono.error(new CartItemNotFoundException(productId)))
                .map(item -> item.withQuantity(request.quantity()))
                .flatMap(item -> cartRepository.saveItem(userId, item));
    }

    public Mono<Void> removeItem(String userId, String productId) {
        return cartRepository.findItem(userId, productId)
                .switchIfEmpty(Mono.error(new CartItemNotFoundException(productId)))
                .flatMap(item -> cartRepository.removeItem(userId, productId));
    }

    public Mono<Void> clearCart(String userId) {
        return cartRepository.clearCart(userId);
    }
}