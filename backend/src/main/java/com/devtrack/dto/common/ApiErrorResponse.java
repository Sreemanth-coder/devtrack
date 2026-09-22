package com.devtrack.dto.common;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Uniform error body for every non-2xx response.
 * fieldErrors is populated only for validation failures.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {
    public record FieldErrorDetail(String field, String message) {
    }

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse ofValidation(int status, String error, String message, String path,
                                                 Map<String, String> fieldErrors) {
        List<FieldErrorDetail> details = fieldErrors.entrySet().stream()
                .map(e -> new FieldErrorDetail(e.getKey(), e.getValue()))
                .toList();
        return new ApiErrorResponse(Instant.now(), status, error, message, path, details);
    }
}
