package com.ecommerce.crtdev.rag_service.application.services;

import com.ecommerce.crtdev.rag_service.application.ports.out.EmbeddingPort;
import com.ecommerce.crtdev.rag_service.application.ports.out.TextGenerationPort;
import com.ecommerce.crtdev.rag_service.application.ports.out.VectorStorePort;
import com.ecommerce.crtdev.rag_service.domain.model.SearchStreamEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SearchProductsService {
    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final TextGenerationPort textGenerationPort;

    public SearchProductsService(EmbeddingPort embeddingPort, VectorStorePort vectorStorePort, TextGenerationPort textGenerationPort) {
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
        this.textGenerationPort = textGenerationPort;
    }

    public Flux<SearchStreamEvent> search(String userQuery) {
        return embeddingPort.embed(userQuery)
                .flatMap(userQueryEmbedded -> vectorStorePort.findSimilar(userQueryEmbedded, 3))
                .flatMapMany(products -> {
                    SearchStreamEvent sourcesEvent = SearchStreamEvent.sources(products);

                    Flux<SearchStreamEvent> textStream = textGenerationPort.generate(userQuery, products)
                            .map(SearchStreamEvent::chunk);

                    return Flux.just(sourcesEvent).concatWith(textStream);
                });
    }
}
