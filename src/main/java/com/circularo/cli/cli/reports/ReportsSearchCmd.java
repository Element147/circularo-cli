package com.circularo.cli.cli.reports;

import com.circularo.cli.api.ReportsApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.WorkflowReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;

/**
 * Picocli sub-command for document workflow report search operations.
 */
@Component
@Command(
        name = "search",
        mixinStandardHelpOptions = true,
        header = "Search document workflow reports.",
        description = {
            "",
            "Queries the Circularo reports database using Elasticsearch query_string syntax.",
            "Finds reports containing participant action states, document details, and workflow progress.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\"NDA Agreement.pdf\"\"",
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
public class ReportsSearchCmd implements Runnable {

    private final ReportsApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Option(names = {"-v", "--verbose"}, description = "Include detailed lists of processed and waiting actions")
    private boolean verbose;

    @Autowired
    public ReportsSearchCmd(ReportsApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        List<WorkflowReport> reports = apiClient.searchReports(query, limit);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatReports(reports, verbose),
                        j -> j.formatReports(reports, verbose),
                        m -> m.formatReports(reports, verbose))
        );
    }
}
