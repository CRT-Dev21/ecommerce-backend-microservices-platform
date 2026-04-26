package com.ecommerce.crtdev.rag_service.adapter.in.kafka.events;

public record ProductCreated (Long sellerId, String productId, String name, String description, String category, double price, int stock) {}
