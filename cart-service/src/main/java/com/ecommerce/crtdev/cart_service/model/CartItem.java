package com.ecommerce.crtdev.cart_service.model;

import java.math.BigDecimal;

public record CartItem(
        String productId,
        String name,
        BigDecimal price,
        String imageUrl,
        int quantity
) {
    public CartItem withQuantity(int newQuantity) {
        return new CartItem(productId, name, price, imageUrl, newQuantity);
    }
}