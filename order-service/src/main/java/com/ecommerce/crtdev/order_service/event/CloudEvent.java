package com.ecommerce.crtdev.order_service.event;

public record CloudEvent <T> (EventMetadata metadata, T payload){
}
