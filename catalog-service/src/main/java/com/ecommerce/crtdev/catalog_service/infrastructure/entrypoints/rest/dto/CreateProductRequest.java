package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @Positive(message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Category is required")
        String categoryId,

        @PositiveOrZero
        int stock
) {}
