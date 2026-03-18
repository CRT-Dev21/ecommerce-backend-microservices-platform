package com.ecommerce.crtdev.payment_service.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventMetadata(
        UUID eventId,
        String  eventType,
        String  schemaVersion,
        String  source,
        Instant timestamp,
        UUID    correlationId,
        UUID    checkoutId,
        UUID    causationId
) {
    public static EventMetadata simple(String eventType) {
        return new EventMetadata(UUID.randomUUID(), eventType, "1.0",
                "payment-service", Instant.now(), null, null, null);
    }

    public static EventMetadata saga(String eventType, UUID correlationId,
                                     UUID checkoutId, UUID causationId) {
        return new EventMetadata(UUID.randomUUID(), eventType, "1.0",
                "payment-service", Instant.now(), correlationId, checkoutId, causationId);
    }
}
