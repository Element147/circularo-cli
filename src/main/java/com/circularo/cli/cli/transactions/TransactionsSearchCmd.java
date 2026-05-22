package com.circularo.cli.cli.transactions;

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
 * Picocli sub-command for document workflow transaction search operations.
 */
@Component
@Command(
        name = "search",
        mixinStandardHelpOptions = true,
        header = "Search and filter document workflow transactions.",
        description = {
            "",
            "Queries the Circularo reports database using Elasticsearch query_string syntax and filters by state.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\"NDA Agreement.pdf\"\"",
            "  - Wildcard title search:      -q \"title:Invoice*\"",
            "  - Filter by transaction state: --state completed",
            "",
            "Formatting Options:",
            "  - Use '-f table' (default) to show transactions in a readable grid.",
            "  - Use '-f json' to output raw JSON objects for integration scripts.",
            "  - Use '-f md' to output GFM (GitHub Flavored Markdown) tables."
        },
        footer = {
            "",
            "Refer to 'docs/user_manual.md' for the complete user manual and troubleshooting steps."
        }
)
public class TransactionsSearchCmd implements Runnable {

    private final ReportsApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"-s", "--state"}, description = "Filter transactions by state (e.g. completed, inprogress, draft, cancelled)")
    private String state;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Option(names = {"-v", "--verbose"}, description = "Include detailed lists of processed and waiting actions")
    private boolean verbose;

    @Autowired
    public TransactionsSearchCmd(ReportsApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        List<WorkflowReport> reports = apiClient.searchReports(query, limit);

        if (state != null && !state.isBlank()) {
            reports = reports.stream()
                    .filter(r -> r.shareStateName() != null && r.shareStateName().equalsIgnoreCase(state))
                    .toList();
        }

        final List<WorkflowReport> results = reports;
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatTransactions(results, verbose),
                        j -> j.formatTransactions(results, verbose),
                        m -> m.formatTransactions(results, verbose))
        );
    }
}
