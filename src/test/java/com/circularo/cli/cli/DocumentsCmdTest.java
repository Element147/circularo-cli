package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentsCmdTest {

    private StubApiClient apiClient;
    private TableFormatter tableFormatter;
    private JsonFormatter jsonFormatter;
    private DocumentsCmd documentsCmd;

    @BeforeEach
    void setUp() {
        apiClient = new StubApiClient();
        tableFormatter = new TableFormatter();
        jsonFormatter = new JsonFormatter();
        documentsCmd = new DocumentsCmd(apiClient, tableFormatter, jsonFormatter);
    }

    @Test
    void run_withTableFormat_queriesApiAndPrintsTable() {
        List<Document> mockDocs = List.of(new Document("doc-1", "Invoice 123", "invoice", "2026-05-18", null));
        apiClient.setStubDocs(mockDocs);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(documentsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "Invoice*", "-f", "table");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("Invoice 123"));
        assertTrue(result.contains("invoice"));
    }

    @Test
    void run_withJsonFormat_queriesApiAndPrintsJson() {
        List<Document> mockDocs = List.of(new Document("doc-1", "Invoice 123", "invoice", "2026-05-18", null));
        apiClient.setStubDocs(mockDocs);

        StringWriter out = new StringWriter();
        CommandLine cmd = new CommandLine(documentsCmd);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "Invoice*", "-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Invoice 123\""));
        assertTrue(result.contains("\"type\" : \"invoice\""));
    }
}
