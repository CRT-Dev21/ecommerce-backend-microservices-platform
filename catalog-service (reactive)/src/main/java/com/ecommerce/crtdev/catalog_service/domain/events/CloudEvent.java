package com.ecommerce.crtdev.catalog_service.domain.events;

public record CloudEvent <T> (EventMetadata metadata, T payload){
}
