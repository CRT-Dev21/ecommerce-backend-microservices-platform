package com.ecommerce.crtdev.rag_service.adapter.in.kafka.events;

public record CloudEvent <T> (EventMetadata metadata, T payload){
}
