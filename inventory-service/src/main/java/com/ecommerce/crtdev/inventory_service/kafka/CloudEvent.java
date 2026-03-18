package com.ecommerce.crtdev.inventory_service.kafka;

public record CloudEvent <T> (EventMetadata metadata, T payload){
}
