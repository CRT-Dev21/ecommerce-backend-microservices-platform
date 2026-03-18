package com.ecommerce.crtdev.catalog_service.domain.events;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        String eventType,
        String schemaVersion,
        String source,
        Instant timestamp
) {}
