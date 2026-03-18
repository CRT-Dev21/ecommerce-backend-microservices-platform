package com.ecommerce.crtdev.seller_service.kafka.events;

public record CloudEvent <T> (
        EventMetadata eventMetadata,
        T payload
) {
}
