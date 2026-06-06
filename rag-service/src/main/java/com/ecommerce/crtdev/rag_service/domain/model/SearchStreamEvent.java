package com.ecommerce.crtdev.rag_service.domain.model;

import java.util.List;

public record SearchStreamEvent(
        Type type,
        List<ProductDocument> sources,
        String textChunk
){
    public enum Type {
        SOURCES, CHUNK
    }
    public static SearchStreamEvent sources(List<ProductDocument> sources){
        return new SearchStreamEvent(Type.SOURCES, sources, null);
    }

    public static SearchStreamEvent chunk (String text){
        return new SearchStreamEvent(Type.CHUNK, null, text);
    }
}
