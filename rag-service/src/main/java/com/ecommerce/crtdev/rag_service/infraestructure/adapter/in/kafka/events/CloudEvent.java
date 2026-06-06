package com.ecommerce.crtdev.rag_service.infraestructure.adapter.in.kafka.events;

public record CloudEvent <T> (EventMetadata metadata, T payload){
}
