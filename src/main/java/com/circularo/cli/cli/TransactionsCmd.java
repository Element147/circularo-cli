package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoApiClient;
import com.circularo.cli.format.JsonFormatter;
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
 * Picocli sub-command for document workflow transaction operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies.
 */
@Component
@Command(
        name = "transactions",
        mixinStandardHelpOptions = true,
        header = "Search and filter document workflow transactions.",
        description = {
            "",
            "Queries the Circularo reports database using Elasticsearch query_string syntax and filters by state.",
            "",
            "Supported Query Examples:",
            "  - Match by exact title:       -q \"title:\\\"NDA Agreement.pdf\\\"\"",
            "  - Wildcard title search:      -q \"title:Invoice*\"",
            "  - Filter by transaction state: --state completed",
            "",
            "Formatting Options:",
            "  - Use '-f table' (default) to show transactions in a readable grid.",
            "  - Use '-f json' to output raw JSON objects for integration scripts."
        },
        footer = {
            "",
            "Refer to 'docs/user_manual.md' for the complete user manual and troubleshooting steps."
        }
)
public class TransactionsCmd implements Runnable {

    private final CircularoApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"-s", "--state"}, description = "Filter transactions by state (e.g. completed, inprogress, draft, cancelled)")
    private String state;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json)", defaultValue = "table")
    private String format;

    @Autowired
    public TransactionsCmd(CircularoApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
    }

    @Override
    public void run() {
        List<WorkflowReport> reports = apiClient.searchReports(query);

        if (state != null && !state.isBlank()) {
            reports = reports.stream()
                    .filter(r -> r.shareStateName() != null && r.shareStateName().equalsIgnoreCase(state))
                    .toList();
        }

        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatTransactions(reports);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatTransactions(reports);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }

        spec.commandLine().getOut().println(formattedOutput);
    }
}
