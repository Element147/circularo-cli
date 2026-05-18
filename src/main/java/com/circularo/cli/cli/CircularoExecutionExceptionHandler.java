package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoException;
import com.circularo.cli.api.CircularoCredentialsException;
import com.circularo.cli.api.CircularoAuthorizationException;
import com.circularo.cli.api.CircularoMalformedInputException;
import com.circularo.cli.api.CircularoUpstreamException;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

/**
 * Custom exception handler for Picocli command execution.
 * Intercepts business exceptions and formats clear, actionable error messages for the user,
 * completely suppressing ugly stack traces unless in debug scenarios.
 */
public class CircularoExecutionExceptionHandler implements IExecutionExceptionHandler {

    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult parseResult) {
        if (ex instanceof CircularoException) {
            String prefix = "Error: ";
            if (ex instanceof CircularoCredentialsException) {
                prefix = "Authentication Error: ";
            } else if (ex instanceof CircularoAuthorizationException) {
                prefix = "Authorization Error: ";
            } else if (ex instanceof CircularoMalformedInputException) {
                prefix = "Validation Error: ";
            } else if (ex instanceof CircularoUpstreamException) {
                prefix = "Upstream Service Error: ";
            }
            
            commandLine.getErr().println(prefix + ex.getMessage());
            return 1;
        }

        // Unexpected system exceptions
        commandLine.getErr().println("Unexpected System Error: " + ex.getMessage());
        return 2;
    }
}
