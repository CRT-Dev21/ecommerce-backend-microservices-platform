package com.ecommerce.crtdev.rag_service.application.port.out;

public interface EmbeddingPort {
    float[] embed(String text);
}
