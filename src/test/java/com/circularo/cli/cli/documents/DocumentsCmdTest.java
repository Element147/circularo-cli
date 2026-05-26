package com.circularo.cli.cli.documents;

import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
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

    private StubDocumentsApiClient apiClient;
    private OutputHelper outputHelper;

    @BeforeEach
    void setUp() {
        apiClient = new StubDocumentsApiClient();
        outputHelper = new OutputHelper(new TableFormatter(), new JsonFormatter(), new MarkdownFormatter());
    }

    @Test
    void run_withTableFormat_queriesApiAndPrintsTable() {
        List<Document> mockDocs = List.of(new Document("doc-1", "Invoice 123", "invoice", "2026-05-18", null));
        apiClient.setStubDocs(mockDocs);

        StringWriter out = new StringWriter();
        DocumentsSearchCmd cmdObj = new DocumentsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
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
        DocumentsSearchCmd cmdObj = new DocumentsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "Invoice*", "-f", "json");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("\"documentTitle\" : \"Invoice 123\""));
        assertTrue(result.contains("\"type\" : \"invoice\""));
    }

    @Test
    void run_withMarkdownFormat_queriesApiAndPrintsMarkdown() {
        List<Document> mockDocs = List.of(new Document("doc-1", "Invoice 123", "invoice", "2026-05-18", null));
        apiClient.setStubDocs(mockDocs);

        StringWriter out = new StringWriter();
        DocumentsSearchCmd cmdObj = new DocumentsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "Invoice*", "-f", "md");

        assertEquals(0, exitCode);
        String result = out.toString();
        assertTrue(result.contains("| Document ID | Title |"));
        assertTrue(result.contains("| doc-1 | Invoice 123 |"));
    }

    @Test
    void run_withEmptyResults_printsNoDocumentsMessage() {
        apiClient.setStubDocs(List.of());

        StringWriter out = new StringWriter();
        DocumentsSearchCmd cmdObj = new DocumentsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("-q", "nonexistent");

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("No documents found."));
    }

    @Test
    void run_withLimitOption_passesLimitAndPrintsTable() {
        List<Document> mockDocs = List.of(new Document("doc-1", "Invoice 123", "invoice", "2026-05-18", null));
        apiClient.setStubDocs(mockDocs);

        StringWriter out = new StringWriter();
        DocumentsSearchCmd cmdObj = new DocumentsSearchCmd(apiClient, outputHelper);
        CommandLine cmd = new CommandLine(cmdObj);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute("--limit", "10", "-f", "table");

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Invoice 123"));
    }

    @Test
    void run_documentsCmd_printsUsage() {
        CommandLine.IFactory factory = new CommandLine.IFactory() {
            @Override
            @SuppressWarnings("unchecked")
            public <K> K create(Class<K> cls) throws Exception {
                if (cls == DocumentsSearchCmd.class) return (K) new DocumentsSearchCmd(apiClient, outputHelper);
                return CommandLine.defaultFactory().create(cls);
            }
        };

        StringWriter out = new StringWriter();
        DocumentsCmd cmdObj = new DocumentsCmd();
        CommandLine cmd = new CommandLine(cmdObj, factory);
        cmd.setOut(new PrintWriter(out));

        int exitCode = cmd.execute();

        assertEquals(0, exitCode);
        assertTrue(out.toString().contains("Search and manage Circularo documents."));
    }
}
