package com.devtrack.exception;

import org.springframework.http.HttpStatus;

/**
 * Base for all deliberate, handled application exceptions.
 * Anything unchecked and unexpected falls through to the generic
 * 500 handler in GlobalExceptionHandler instead of extending this.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
