package com.ecommerce.crtdev.catalog_service.application.commands;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;


public record CreateProductCommand (
        Long sellerId,
        String name,
        String description,
        double price,
        String categoryId,
        int stock,
        Flux<DataBuffer> image
) {}
