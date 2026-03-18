package com.ecommerce.crtdev.notification_service.event;

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
) {}
