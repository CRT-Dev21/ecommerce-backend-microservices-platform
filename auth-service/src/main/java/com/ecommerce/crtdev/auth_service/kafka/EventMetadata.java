package com.ecommerce.crtdev.auth_service.kafka;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        String eventType,
        String schemaVersion,
        String source,
        Instant timestamp
) {}