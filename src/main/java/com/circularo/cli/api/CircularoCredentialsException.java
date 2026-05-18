package com.circularo.cli.api;

/**
 * Exception thrown when authentication or credential checks fail.
 * Typically maps to an HTTP 401 Unauthorized response or a missing token.
 */
public class CircularoCredentialsException extends CircularoException {

    public CircularoCredentialsException(String message) {
        super(message);
    }

    public CircularoCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
