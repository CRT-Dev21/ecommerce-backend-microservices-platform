package com.ecommerce.crtdev.rag_service.domain.model;

public record ProductDocument (
        String productId,
        String name,
        String description,
        String category,
        double price
){
}
