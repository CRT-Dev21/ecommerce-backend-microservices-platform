package com.ecommerce.crtdev.rag_service.application.ports.in;

import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import reactor.core.publisher.Mono;

public interface IndexProductUseCase {
    Mono<Void> index(ProductDocument product);
    Mono<Void> delete(String productId);
}
