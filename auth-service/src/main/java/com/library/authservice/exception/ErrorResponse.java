package com.library.authservice.exception;

import java.time.LocalDateTime;

/**
 * Estructura de respuesta de error estandarizada para Auth-Service.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {}
