package com.ecommerce.crtdev.rag_service.application.port.out;

import com.ecommerce.crtdev.rag_service.domain.ProductDocument;

import java.util.List;

public interface VectorStorePort {
    void upsert(ProductDocument productDocument, float[] embedding);
    void delete(String productId);
    List<ProductDocument> findSimilar (float[] queryEmbedding, int topK);
}
