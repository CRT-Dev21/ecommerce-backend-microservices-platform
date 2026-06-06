package com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.dto;

import java.util.List;

public record OllamaEmbedResponse(List<List<Float>> embeddings) {
}
