package com.evaluacion.tvmaze.dto;

import java.time.Instant;
import java.util.List;

/**
 * Formato común para todas las respuestas de error del API.
 */
public record ApiError(
        String status,
        int code,
        String message,
        List<String> details,
        Instant timestamp) {

    public static ApiError of(int code, String message) {
        return of(code, message, List.of());
    }

    public static ApiError of(int code, String message, List<String> details) {
        return new ApiError("error", code, message, details, Instant.now());
    }
}
