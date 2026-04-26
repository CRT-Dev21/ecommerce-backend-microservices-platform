package com.ecommerce.crtdev.rag_service.application.port.out;

import com.ecommerce.crtdev.rag_service.domain.ProductDocument;

import java.util.List;

public interface TextGenerationPort {
    String generate(String userQuery, List<ProductDocument> context);
}
