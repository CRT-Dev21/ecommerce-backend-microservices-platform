package com.ecommerce.crtdev.seller_service.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID    eventId,
        String  eventType,
        String  schemaVersion,
        String  source,
        Instant timestamp
) {
    public static EventMetadata of(String eventType) {
        return new EventMetadata(
                UUID.randomUUID(), eventType, "1.0", "seller-service", Instant.now()
        );
    }
}