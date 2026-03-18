package com.ecommerce.crtdev.auth_service.kafka;

public record CloudEvent <T> (
        EventMetadata eventMetadata,
        T payload
) {
}
