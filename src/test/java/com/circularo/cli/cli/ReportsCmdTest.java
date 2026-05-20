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

class ReportsCmdTest {

    private StubApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private ReportsCmd reportsCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        reportsCmd = new ReportsCmd(apiClient, tableFormatter, jsonFormatter);
    }

    @Test
    void run_withTableFormat_queriesApiAndPrintsTable() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Report Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 2, 0, "Lubos", "Lubos"
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(reportsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "title:Report*", "-f", "table");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Report Title"));
        assertTrue(result.contains("Lubos"));
        assertTrue(result.contains("2/0/2"));
    }

    @Test
    void run_withJsonFormat_queriesApiAndPrintsJson() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Report Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 2, 0, "Lubos", "Lubos"
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(reportsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "title:Report*", "-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Report Title\""));
        assertTrue(result.contains("\"owner\" : \"Lubos\""));
    }
}
