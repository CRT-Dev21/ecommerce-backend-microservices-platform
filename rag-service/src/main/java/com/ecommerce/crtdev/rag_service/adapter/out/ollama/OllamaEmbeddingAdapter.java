package com.ecommerce.crtdev.rag_service.adapter.out.ollama;

import com.ecommerce.crtdev.rag_service.application.port.out.EmbeddingPort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
public class OllamaEmbeddingAdapter implements EmbeddingPort {
    private final EmbeddingModel embeddingModel;

    public OllamaEmbeddingAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }
}
