package com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.ollama;

import com.ecommerce.crtdev.rag_service.application.ports.out.EmbeddingPort;
import com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.dto.OllamaEmbedRequest;
import com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.dto.OllamaEmbedResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class OllamaEmbeddingAdapter implements EmbeddingPort {
    private final WebClient webClient;
    private final String model;

    public OllamaEmbeddingAdapter(WebClient.Builder webClientBuilder,
                                  @Value("${spring.ai.ollama.embedding.model}") String model) {
        this.webClient = webClientBuilder.baseUrl("http://ollama:11434").build();
        this.model = model;
    }

    @Override
    public Mono<float[]> embed(String text) {
        return this.webClient.post()
                .uri("/api/embed")
                .bodyValue(new OllamaEmbedRequest(model, text))
                .retrieve()
                .bodyToMono(OllamaEmbedResponse.class)
                .map(response -> {
                    List<Float> vector = response.embeddings().get(0);
                    float[] floatArray = new float[vector.size()];

                    for(int i =0; i < vector.size(); i++){
                        floatArray[i] = vector.get(i);
                    }
                    return floatArray;
                });
    }
}
