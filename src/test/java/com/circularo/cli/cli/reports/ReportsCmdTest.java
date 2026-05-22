package com.circularo.cli.cli.reports;

import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.WorkflowReportItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportsCmdTest {

    private StubReportsApiClient apiClient;
    private OutputHelper outputHelper;

    @BeforeEach
    void setUp() {
        apiClient = new StubReportsApiClient();
        outputHelper = new OutputHelper(new TableFormatter(), new JsonFormatter(), new MarkdownFormatter());
    }

    @Test
    void run_withTableFormat_queriesApiAndPrintsTable() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Report Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 2, 0, "Lubos", "Lubos", List.of(), List.of()
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        ReportsSearchCmd cmdObj = new ReportsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
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
                120, "finished", 2, 2, 0, "Lubos", "Lubos", List.of(), List.of()
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        ReportsSearchCmd cmdObj = new ReportsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "title:Report*", "-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Report Title\""));
        assertTrue(result.contains("\"owner\" : \"Lubos\""));
    }

    @Test
    void run_withMarkdownFormat_queriesApiAndPrintsMarkdown() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Report Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 2, 0, "Lubos", "Lubos", List.of(), List.of()
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        ReportsSearchCmd cmdObj = new ReportsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "title:Report*", "-f", "md");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("| Document ID | Title |"));
        assertTrue(result.contains("| doc-1 | Report Title |"));
    }

    @Test
    void run_withVerboseMode_printsDetailedParticipants() {
        List<WorkflowReportItem> processed = List.of(
            new WorkflowReportItem("user1@example.com", "sign", "2026-05-18T10:00:00Z", true, 60, null, null, null, false, false, 1, "share-1", "verified")
        );
        List<WorkflowReportItem> waiting = List.of(
            new WorkflowReportItem("user2@example.com", "approve", null, false, null, null, null, "2026-05-18T10:01:00Z", false, false, 2, "share-2", null)
        );
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Report Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 1, 1, "Lubos", "Lubos", processed, waiting
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        ReportsSearchCmd cmdObj = new ReportsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "title:Report*", "-v");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Processed Actions:"));
        assertTrue(result.contains("user1@example.com [sign]"));
        assertTrue(result.contains("Waiting For:"));
        assertTrue(result.contains("user2@example.com [approve]"));
    }

    @Test
    void run_reportsCmd_printsUsage() {
        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == ReportsSearchCmd.class) return (K) new ReportsSearchCmd(apiClient, outputHelper);
                return CommandLine.defaultFactory().create(cls);
            }
        };

        StringWriter out = new StringWriter();
        ReportsCmd cmdObj = new ReportsCmd();
        CommandLine cmd = new CommandLine(cmdObj, factory);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute();

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Generate and search document workflow reports."));
    }
}
