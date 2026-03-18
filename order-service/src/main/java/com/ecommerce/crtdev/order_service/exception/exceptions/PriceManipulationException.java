package com.ecommerce.crtdev.order_service.exception.exceptions;

import java.math.BigDecimal;

public class PriceManipulationException extends RuntimeException {
    public PriceManipulationException(String id, BigDecimal expected, BigDecimal received) {
        super("Price mismatch for product " + id
                + ": expected " + expected + " received " + received); }
}