package com.ecommerce.crtdev.rag_service.application.service;

import com.ecommerce.crtdev.rag_service.application.port.in.IndexProductUseCase;
import com.ecommerce.crtdev.rag_service.application.port.out.EmbeddingPort;
import com.ecommerce.crtdev.rag_service.application.port.out.VectorStorePort;
import com.ecommerce.crtdev.rag_service.domain.ProductDocument;
import org.springframework.stereotype.Service;

@Service
public class IndexingService implements IndexProductUseCase {

    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;

    public IndexingService(EmbeddingPort embeddingPort, VectorStorePort vectorStorePort) {
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
    }

    @Override
    public void index(ProductDocument product) {
        String textToEmbed = """
            Product name: %s
            Category: %s
            Description: %s
            Price: $%.2f
            """.formatted(
                product.name(),
                product.category(),
                product.description(),
                product.price()
        );
        float[] embedding = embeddingPort.embed(textToEmbed);
        vectorStorePort.upsert(product, embedding);
    }

    @Override
    public void delete(String productId) {
        vectorStorePort.delete(productId);
    }
}
