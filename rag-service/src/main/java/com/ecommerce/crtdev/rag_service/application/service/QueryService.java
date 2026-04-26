package com.ecommerce.crtdev.rag_service.application.service;

import com.ecommerce.crtdev.rag_service.application.port.in.SearchProductsUseCase;
import com.ecommerce.crtdev.rag_service.application.port.out.*;
import com.ecommerce.crtdev.rag_service.domain.ProductDocument;
import com.ecommerce.crtdev.rag_service.domain.SearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService implements SearchProductsUseCase {
    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final TextGenerationPort textGenerationPort;

    public QueryService(EmbeddingPort embeddingPort, VectorStorePort vectorStorePort, TextGenerationPort textGenerationPort) {
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
        this.textGenerationPort = textGenerationPort;
    }

    @Override
    public SearchResult search(String userQuery) {
        float[] queryEmbedding = embeddingPort.embed(userQuery);
        List<ProductDocument> similarProducts = vectorStorePort.findSimilar(queryEmbedding, 5);
        String answer = textGenerationPort.generate(userQuery, similarProducts);

        return new SearchResult(answer, similarProducts);
    }
}
