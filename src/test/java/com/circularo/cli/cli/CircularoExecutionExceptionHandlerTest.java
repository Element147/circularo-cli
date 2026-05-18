package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoCredentialsException;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.TableFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircularoExecutionExceptionHandlerTest {

    private StubApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private UsersCmd usersCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        usersCmd = new UsersCmd(apiClient, tableFormatter, jsonFormatter);
    }

    @Test
    void execute_withCircularoException_printsFriendlyMessageAndReturnsOne() {
        apiClient.setException(new CircularoCredentialsException("Invalid token provided"));

        StringWriter err = new StringWriter();
        CommandLine cmd = new CommandLine(usersCmd);
        cmd.setErr(new PrintWriter(err));
        cmd.setExecutionExceptionHandler(new CircularoExecutionExceptionHandler());

        int exitCode = cmd.execute("-q", "mary");

        assertEquals(1, exitCode);
        assertTrue(err.toString().contains("Authentication Error: Invalid token provided"));
    }

    @Test
    void execute_withGenericRuntimeException_printsSystemErrorAndReturnsTwo() {
        apiClient.setException(new RuntimeException("Something broke"));

        StringWriter err = new StringWriter();
        CommandLine cmd = new CommandLine(usersCmd);
        cmd.setErr(new PrintWriter(err));
        cmd.setExecutionExceptionHandler(new CircularoExecutionExceptionHandler());

        int exitCode = cmd.execute("-q", "mary");

        assertEquals(2, exitCode);
        assertTrue(err.toString().contains("Unexpected System Error: Something broke"));
    }
}
