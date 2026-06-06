package com.ecommerce.crtdev.rag_service.application.ports.out;

import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TextGenerationPort {
    Flux<String> generate(String userQuery, List<ProductDocument> context);
}
