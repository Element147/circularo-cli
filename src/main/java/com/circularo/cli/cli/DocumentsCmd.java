package com.circularo.cli.cli;

import com.circularo.cli.api.DocumentsApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;

/**
 * Picocli sub-command for document search operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies when they are added.
 */
@Component
@Command(
        name = "documents",
        mixinStandardHelpOptions = true,
        header = "Search and retrieve Circularo documents.",
        description = {
            "",
            "Queries the Circularo documents repository using Elasticsearch query_string syntax.",
            "Finds documents based on titles, status, metadata, tags, and creator info.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\\\"NDA Agreement.pdf\\\"\"",
            "  - Wildcard title search:      -q \"title:Invoice*\"",
            "  - Filter by document status:  -q \"status:signed\"",
            "  - Multiple filters combined:  -q \"status:inprogress AND title:*Contract*\"",
            "",
            "Formatting Options:",
            "  - Use '-f table' (default) to show results in a readable grid.",
            "  - Use '-f json' to output raw JSON objects for integration scripts.",
            "  - Use '-f md' to output GFM (GitHub Flavored Markdown) tables."
        },
        footer = {
            "",
            "Refer to 'docs/user_manual.md' for the complete user manual and troubleshooting steps."
        }
)
public class DocumentsCmd implements Runnable {

    private final DocumentsApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public DocumentsCmd(DocumentsApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        List<Document> documents = apiClient.searchDocuments(query, limit);
        
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatDocuments(documents);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatDocuments(documents);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatDocuments(documents);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }

        spec.commandLine().getOut().println(formattedOutput);
    }
}
