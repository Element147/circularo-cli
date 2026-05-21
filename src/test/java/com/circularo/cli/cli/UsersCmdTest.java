package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsersCmdTest {

    private StubUsersApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private MarkdownFormatter markdownFormatter;
    private UsersCmd usersCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubUsersApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        markdownFormatter = new MarkdownFormatter();
        usersCmd = new UsersCmd(apiClient, tableFormatter, jsonFormatter, markdownFormatter);
    }

    @Test
    void run_withTableFormat_queriesApiAndPrintsTable() {
        List<User> mockUsers = List.of(new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active"));
        apiClient.setStubUsers(mockUsers);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(usersCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "mary", "-f", "table");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Mary Griffin"));
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("active"));
    }

    @Test
    void run_withJsonFormat_queriesApiAndPrintsJson() {
        List<User> mockUsers = List.of(new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active"));
        apiClient.setStubUsers(mockUsers);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(usersCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "mary", "-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"fullname\" : \"Mary Griffin\""));
        assertTrue(result.contains("\"role\" : \"admin\""));
    }

    @Test
    void run_withMarkdownFormat_queriesApiAndPrintsMarkdown() {
        List<User> mockUsers = List.of(new User("1", "Mary Griffin", "mary@example.com", "mary", null, null, "admin", "active"));
        apiClient.setStubUsers(mockUsers);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(usersCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "mary", "-f", "md");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("| ID/Email | Full Name |"));
        assertTrue(result.contains("| 1 | Mary Griffin |"));
    }
}
