package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoCredentialsException;
import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.cli.users.UsersSearchCmd;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.User;
import com.circularo.cli.model.UserModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircularoExecutionExceptionHandlerTest {

    private StubUsersApiClient apiClient;
    private OutputHelper outputHelper;
    private UsersSearchCmd usersSearchCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubUsersApiClient();
        outputHelper = new OutputHelper(new TableFormatter(), new JsonFormatter(), new MarkdownFormatter());
        usersSearchCmd = new UsersSearchCmd(apiClient, outputHelper);
    }

    @Test
    void execute_withCircularoException_printsFriendlyMessageAndReturnsOne() {
        apiClient.setException(new CircularoCredentialsException("Invalid token provided"));

        StringWriter err = new StringWriter();
        CommandLine cmd = new CommandLine(usersSearchCmd);
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
        CommandLine cmd = new CommandLine(usersSearchCmd);
        cmd.setErr(new PrintWriter(err));
        cmd.setExecutionExceptionHandler(new CircularoExecutionExceptionHandler());

        int exitCode = cmd.execute("-q", "mary");

        assertEquals(2, exitCode);
        assertTrue(err.toString().contains("Unexpected System Error: Something broke"));
    }

    /**
     * A package-local stub kept in the same package as the test so
     * that CircularoExecutionExceptionHandlerTest can access it.
     */
    static class StubUsersApiClient extends UsersApiClient {
        private RuntimeException exceptionToThrow;

        public StubUsersApiClient() {
            super(null);
        }

        public void setException(RuntimeException ex) {
            this.exceptionToThrow = ex;
        }

        @Override
        public List<User> searchUsers(String query, int limit) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return List.of();
        }
    }
}
