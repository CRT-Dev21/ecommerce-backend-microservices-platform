package com.ecommerce.crtdev.rag_service.application.ports.out;

import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VectorStorePort {
    Mono<Void> upsert(ProductDocument productDocument, float[] embedding);
    Mono<Void> delete(String productId);
    Mono<List<ProductDocument>> findSimilar (float[] queryEmbedding, int topK);
}
