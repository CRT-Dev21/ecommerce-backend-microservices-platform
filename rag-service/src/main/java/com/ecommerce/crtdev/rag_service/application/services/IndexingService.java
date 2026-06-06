package com.ecommerce.crtdev.rag_service.application.services;

import com.ecommerce.crtdev.rag_service.application.ports.out.EmbeddingPort;
import com.ecommerce.crtdev.rag_service.application.ports.out.VectorStorePort;
import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IndexingService {

    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;

    public IndexingService(EmbeddingPort embeddingPort, VectorStorePort vectorStorePort) {
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
    }

    public Mono<Void> index(ProductDocument product) {
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
        return embeddingPort.embed(textToEmbed).flatMap(embedding -> vectorStorePort.upsert(product, embedding));
    }

    public Mono<Void> delete(String productId) {
        return vectorStorePort.delete(productId);
    }
}
