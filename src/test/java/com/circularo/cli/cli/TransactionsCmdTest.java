package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.WorkflowReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransactionsCmdTest {

    private StubApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private TransactionsCmd transactionsCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        transactionsCmd = new TransactionsCmd(apiClient, tableFormatter, jsonFormatter);
    }

    @Test
    void run_withStateFilter_filtersResultsCorrectly() {
        List<WorkflowReport> mockReports = List.of(
                new WorkflowReport("doc-1", "Completed Tx", "completed", "2026-05-18", "2026-05-19",
                        120, "finished", 2, 0, 2, "Lubos", "Lubos"),
                new WorkflowReport("doc-2", "In Progress Tx", "inprogress", "2026-05-18", null,
                        0, null, 2, 1, 2, "Lubos", "Lubos")
        );
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(transactionsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-s", "completed");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Completed Tx"));
        assertFalse(result.contains("In Progress Tx"));
    }

    @Test
    void run_withJsonFormat_queriesApiAndPrintsJson() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Tx Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 0, 2, "Lubos", "Lubos"
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(transactionsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Tx Title\""));
        assertTrue(result.contains("\"shareStateName\" : \"completed\""));
    }
}
