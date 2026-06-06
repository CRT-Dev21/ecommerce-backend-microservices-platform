package com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.ollama;

import com.ecommerce.crtdev.rag_service.application.ports.out.TextGenerationPort;
import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OllamaGenerationAdapter implements TextGenerationPort {

    private final ChatModel chatModel;

    public OllamaGenerationAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Flux<String> generate(String userQuery, List<ProductDocument> context) {
        String productContext = context.stream()
                .map(p -> "- %s: %s (price: $%.2f)".formatted(p.name(), p.description(), p.price()))
                .collect(Collectors.joining("\n"));

        var system = new SystemMessage("""
                You are a shopping assistant for an eCommerce site.
                Respond only based on the products in the provided catalog.
                If no product is relevant, state so clearly.
                Do not invent products or prices.
                Provide a complete, conclusive recommendation in your response.
                Do not ask follow-up questions or offer multi-turn interactions, as the user cannot reply.
                Respond in the same language as the user. Products available in the catalog:
                """ + productContext);

        var user = new UserMessage(userQuery);
        var prompt = new Prompt(List.of(system, user));

        return chatModel.stream(prompt)
                .map(chatResponse -> {
                    if(chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null){
                        return chatResponse.getResult().getOutput().getText();
                    }
                    return "";
                })
                .filter(text -> !text.isEmpty());
    }
}
