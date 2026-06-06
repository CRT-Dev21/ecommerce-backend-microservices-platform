package com.ecommerce.crtdev.rag_service.application.ports.out;

import reactor.core.publisher.Mono;

public interface EmbeddingPort {
    Mono<float[]> embed(String text);
}
