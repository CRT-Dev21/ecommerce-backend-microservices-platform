package com.ecommerce.crtdev.order_service.exception;

import com.ecommerce.crtdev.order_service.exception.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleNotFound(OrderNotFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        p.setType(URI.create("order/not-found")); return p;
    }
    @ExceptionHandler({OrderNotCancellableException.class, OrderNotRefundableException.class})
    public ProblemDetail handleInvalidTransition(RuntimeException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        p.setType(URI.create("order/invalid-transition")); return p;
    }
    @ExceptionHandler(ProductUnavailableException.class)
    public ProblemDetail handleUnavailable(ProductUnavailableException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        p.setType(URI.create("order/product-unavailable")); return p;
    }
    @ExceptionHandler(PriceManipulationException.class)
    public ProblemDetail handlePrice(PriceManipulationException ex) {
        log.warn("Price manipulation detected: {}", ex.getMessage());
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        p.setType(URI.create("order/price-mismatch")); return p;
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleStock(InsufficientStockException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        p.setType(URI.create("order/insufficient-stock")); return p;
    }
    @ExceptionHandler(InventoryUnavailableException.class)
    public ProblemDetail handleInventory(InventoryUnavailableException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        p.setType(URI.create("order/inventory-unavailable")); return p;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b).orElse("Validation failed");
        ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        p.setType(URI.create("order/validation-error")); return p;
    }
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        p.setType(URI.create("order/internal-error")); return p;
    }

    @ExceptionHandler(InventoryProductNotFoundException.class)
    public ProblemDetail handleInventoryProductNotFound(InventoryProductNotFoundException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        p.setType(URI.create("order/product-not-in-inventory"));
        return p;
    }

    @ExceptionHandler(InventoryConflictException.class)
    public ProblemDetail handleInventoryConflict(InventoryConflictException ex) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        p.setType(URI.create("order/inventory-high-demand"));
        return p;
    }
}
