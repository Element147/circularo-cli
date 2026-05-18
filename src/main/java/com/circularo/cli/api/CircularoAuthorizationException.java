package com.circularo.cli.api;

/**
 * Exception thrown when authorization checks fail (insufficient rights/permissions).
 * Typically maps to an HTTP 403 Forbidden response.
 */
public class CircularoAuthorizationException extends CircularoException {

    public CircularoAuthorizationException(String message) {
        super(message);
    }

    public CircularoAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
