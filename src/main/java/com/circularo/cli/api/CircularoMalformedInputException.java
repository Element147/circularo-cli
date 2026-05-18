package com.circularo.cli.api;

/**
 * Exception thrown when the input provided to the client is malformed or invalid.
 * Typically maps to an HTTP 400 Bad Request response.
 */
public class CircularoMalformedInputException extends CircularoException {

    public CircularoMalformedInputException(String message) {
        super(message);
    }

    public CircularoMalformedInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
