package com.circularo.cli.api;

/**
 * Domain exception for all Circularo API and configuration errors.
 * Extends {@link RuntimeException} so callers are not forced into checked-exception
 * handling boilerplate, while still providing a clear, typed error boundary.
 */
public class CircularoException extends RuntimeException {

    public CircularoException(String message) {
        super(message);
    }

    public CircularoException(String message, Throwable cause) {
        super(message, cause);
    }
}
