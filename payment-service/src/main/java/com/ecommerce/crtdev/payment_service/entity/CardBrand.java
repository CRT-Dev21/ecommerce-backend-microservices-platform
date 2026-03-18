package com.ecommerce.crtdev.payment_service.entity;

public enum CardBrand {
    VISA, MASTERCARD, AMEX, OTHER;

    public static CardBrand detect(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) return OTHER;
        String n = cardNumber.replaceAll("\\s", "");
        if (n.startsWith("4"))                        return VISA;
        if (n.startsWith("5") || n.startsWith("2"))   return MASTERCARD;
        if (n.startsWith("3"))                        return AMEX;
        return OTHER;
    }
}

