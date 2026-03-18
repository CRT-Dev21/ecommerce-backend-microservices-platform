package com.ecommerce.crtdev.cart_service.model;

import java.math.BigDecimal;
import java.util.List;

public record Cart(
        String userId,
        List<CartItem> items
) {
    public BigDecimal total() {
        return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int itemCount() {
        return items.stream().mapToInt(CartItem::quantity).sum();
    }
}
