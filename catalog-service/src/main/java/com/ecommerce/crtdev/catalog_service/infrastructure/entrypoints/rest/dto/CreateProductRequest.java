package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @Positive(message = "Price cannot be negative")
        double price,

        @NotNull(message = "Category is required")
        String categoryId,

        @PositiveOrZero
        int stock
) {}
