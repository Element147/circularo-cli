package com.circularo.cli.cli.transactions;

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
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransactionsCmdTest {

    private StubReportsApiClient apiClient;
    private OutputHelper outputHelper;

    @BeforeEach
    void setUp() {
        apiClient = new StubReportsApiClient();
        outputHelper = new OutputHelper(new TableFormatter(), new JsonFormatter(), new MarkdownFormatter());
    }

    @Test
    void run_withStateFilter_filtersResultsCorrectly() {
        List<WorkflowReport> mockReports = List.of(
                new WorkflowReport("doc-1", "Completed Tx", "completed", "2026-05-18", "2026-05-19",
                        120, "finished", 2, 0, 2, "Lubos", "Lubos", List.of(), List.of()),
                new WorkflowReport("doc-2", "In Progress Tx", "inprogress", "2026-05-18", null,
                        0, null, 2, 1, 2, "Lubos", "Lubos", List.of(), List.of())
        );
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        TransactionsSearchCmd cmdObj = new TransactionsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
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
                120, "finished", 2, 0, 2, "Lubos", "Lubos", List.of(), List.of()
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        TransactionsSearchCmd cmdObj = new TransactionsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Tx Title\""));
        assertTrue(result.contains("\"shareStateName\" : \"completed\""));
    }

    @Test
    void run_withMarkdownFormat_queriesApiAndPrintsMarkdown() {
        List<WorkflowReport> mockReports = List.of(new WorkflowReport(
                "doc-1", "Tx Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 0, 2, "Lubos", "Lubos", List.of(), List.of()
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        TransactionsSearchCmd cmdObj = new TransactionsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-f", "md");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("| Document ID | Title |"));
        assertTrue(result.contains("| doc-1 | Tx Title |"));
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
                "doc-1", "Tx Title", "completed", "2026-05-18", "2026-05-19",
                120, "finished", 2, 1, 1, "Lubos", "Lubos", processed, waiting
        ));
        apiClient.setStubReports(mockReports);

        StringWriter out = new StringWriter();
        TransactionsSearchCmd cmdObj = new TransactionsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-v");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Processed Actions:"));
        assertTrue(result.contains("user1@example.com [sign]"));
        assertTrue(result.contains("Waiting For:"));
        assertTrue(result.contains("user2@example.com [approve]"));
    }

    @Test
    void run_transactionsCmd_printsUsage() {
        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == TransactionsSearchCmd.class) return (K) new TransactionsSearchCmd(apiClient, outputHelper);
                return CommandLine.defaultFactory().create(cls);
            }
        };

        StringWriter out = new StringWriter();
        TransactionsCmd cmdObj = new TransactionsCmd();
        CommandLine cmd = new CommandLine(cmdObj, factory);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute();

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Search and filter document workflow transactions."));
    }
}
