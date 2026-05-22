package com.circularo.cli.cli.documents;

import com.circularo.cli.api.DocumentsApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;

/**
 * Picocli sub-command for document search operations.
 */
@Component
@Command(
        name = "search",
        mixinStandardHelpOptions = true,
        header = "Search and retrieve Circularo documents.",
        description = {
            "",
            "Queries the Circularo documents repository using Elasticsearch query_string syntax.",
            "Finds documents based on titles, status, metadata, tags, and creator info.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\"NDA Agreement.pdf\"\"",
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
public class DocumentsSearchCmd implements Runnable {

    private final DocumentsApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Autowired
    public DocumentsSearchCmd(DocumentsApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        List<Document> documents = apiClient.searchDocuments(query, limit);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatDocuments(documents),
                        j -> j.formatDocuments(documents),
                        m -> m.formatDocuments(documents))
        );
    }
}
