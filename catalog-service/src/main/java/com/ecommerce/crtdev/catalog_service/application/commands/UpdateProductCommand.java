package com.ecommerce.crtdev.catalog_service.application.commands;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

public record UpdateProductCommand (
        String productId,
        Optional<String> name,
        Optional<String> description,
        Optional<BigDecimal> price,
        Optional<Integer> stock,
        Optional<MultipartFile> image
){}
