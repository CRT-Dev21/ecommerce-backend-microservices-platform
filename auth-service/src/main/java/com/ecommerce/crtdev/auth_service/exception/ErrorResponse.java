package com.ecommerce.crtdev.auth_service.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String internalErrorCode,
        String message,
        String uri
) {}
