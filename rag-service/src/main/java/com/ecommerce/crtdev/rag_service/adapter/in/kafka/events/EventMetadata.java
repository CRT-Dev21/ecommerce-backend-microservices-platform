package com.ecommerce.crtdev.rag_service.adapter.in.kafka.events;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        String eventType,
        String schemaVersion,
        String source,
        Instant timestamp
) {}
