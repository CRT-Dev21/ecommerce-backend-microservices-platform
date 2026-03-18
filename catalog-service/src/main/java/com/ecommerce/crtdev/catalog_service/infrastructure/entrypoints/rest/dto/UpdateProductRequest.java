package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto;

public record UpdateProductRequest(
        String name,
        String description,
        Double price,
        Integer stock
) {}
