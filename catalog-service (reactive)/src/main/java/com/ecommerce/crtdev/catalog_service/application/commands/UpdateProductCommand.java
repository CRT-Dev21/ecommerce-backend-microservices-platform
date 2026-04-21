package com.ecommerce.crtdev.catalog_service.application.commands;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

import java.util.Optional;

public record UpdateProductCommand (
        String productId,
        Optional<String> name,
        Optional<String> description,
        Optional<Double> price,
        Optional<Integer> stock,
        Optional<Flux<DataBuffer>> image
){}
