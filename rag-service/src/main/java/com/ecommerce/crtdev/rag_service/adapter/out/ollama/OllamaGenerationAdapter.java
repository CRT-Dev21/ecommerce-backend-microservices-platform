package com.ecommerce.crtdev.rag_service.adapter.out.ollama;

import com.ecommerce.crtdev.rag_service.application.port.out.TextGenerationPort;
import com.ecommerce.crtdev.rag_service.domain.ProductDocument;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OllamaGenerationAdapter implements TextGenerationPort {

    private final ChatModel chatModel;

    public OllamaGenerationAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generate(String userQuery, List<ProductDocument> context) {
        String productContext = context.stream()
                .map(p -> "- %s: %s (price: $%.2f".formatted(p.name(), p.description(), p.price()))
                .collect(Collectors.joining("\n"));

        var system = new SystemMessage("""
                You are a shopping assistant for an eCommerce site.
                Respond only based on the products in the provided catalog.
                If no product is relevant, state so clearly.
                Do not invent products or prices.
                Respond in the same language as the user. Products available in the catalog:
                """ + productContext);

        var user = new UserMessage(userQuery);

        return chatModel.call(new Prompt(List.of(system, user)))
                .getResult()
                .getOutput()
                .getText();
    }
}
