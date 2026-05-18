package com.circularo.cli.api;

/**
 * Exception thrown when the upstream Circularo server returns a server-side error.
 * Typically maps to HTTP 5xx Server Error responses.
 */
public class CircularoUpstreamException extends CircularoException {

    public CircularoUpstreamException(String message) {
        super(message);
    }

    public CircularoUpstreamException(String message, Throwable cause) {
        super(message, cause);
    }
}
