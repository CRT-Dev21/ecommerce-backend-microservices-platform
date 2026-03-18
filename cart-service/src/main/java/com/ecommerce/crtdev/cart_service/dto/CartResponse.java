package com.ecommerce.crtdev.cart_service.dto;

import com.ecommerce.crtdev.cart_service.model.Cart;
import com.ecommerce.crtdev.cart_service.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public sealed interface CartResponse {

    record CartDto(
            String userId,
            List<CartItemDto> items,
            BigDecimal total,
            int itemCount
    ) implements CartResponse {
        public static CartDto from(Cart cart) {
            return new CartDto(
                    cart.userId(),
                    cart.items().stream().map(CartItemDto::from).toList(),
                    cart.total(),
                    cart.itemCount()
            );
        }
    }

    record CartItemDto(
            String productId,
            String name,
            BigDecimal price,
            String imageUrl,
            int quantity,
            BigDecimal subtotal
    ) implements CartResponse {
        public static CartItemDto from(CartItem item) {
            return new CartItemDto(
                    item.productId(),
                    item.name(),
                    item.price(),
                    item.imageUrl(),
                    item.quantity(),
                    item.price().multiply(BigDecimal.valueOf(item.quantity()))
            );
        }
    }
}