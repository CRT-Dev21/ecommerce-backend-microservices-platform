package com.ecommerce.crtdev.catalog_service.application.commands;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;


public record CreateProductCommand (
        Long sellerId,
        String name,
        String description,
        BigDecimal price,
        String categoryId,
        int stock,
        MultipartFile image
) {}
