package com.circularo.cli.cli;

import com.circularo.cli.api.ReportsApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.WorkflowReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;

/**
 * Picocli sub-command for document workflow report operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies.
 */
@Component
@Command(
        name = "reports",
        mixinStandardHelpOptions = true,
        header = "Generate and search document workflow reports.",
        description = {
            "",
            "Queries the Circularo reports database using Elasticsearch query_string syntax.",
            "Finds reports containing participant action states, document details, and workflow progress.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\\\"NDA Agreement.pdf\\\"\"",
            "  - Wildcard title search:      -q \"title:Invoice*\"",
            "  - Combine fields using AND:    -q \"owner:lubo* AND sender:lubo*\"",
            "",
            "Formatting Options:",
            "  - Use '-f table' (default) to show reports in a readable grid.",
            "  - Use '-f json' to output raw JSON objects for integration scripts.",
            "  - Use '-f md' to output GFM (GitHub Flavored Markdown) tables."
        },
        footer = {
            "",
            "Refer to 'docs/user_manual.md' for the complete user manual and troubleshooting steps."
        }
)
public class ReportsCmd implements Runnable {

    private final ReportsApiClient apiClient;
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

    @Option(names = {"-v", "--verbose"}, description = "Include detailed lists of processed and waiting actions")
    private boolean verbose;

    @Autowired
    public ReportsCmd(ReportsApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        List<WorkflowReport> reports = apiClient.searchReports(query, limit);

        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatReports(reports, verbose);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatReports(reports, verbose);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatReports(reports, verbose);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }

        spec.commandLine().getOut().println(formattedOutput);
    }
}
