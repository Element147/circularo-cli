package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.WorkflowReport;
import java.util.List;

/**
 * Common interface for CLI output formatting to ensure deterministic rendering,
 * clean abstractions, and AOT-compliant compilation under GraalVM.
 */
public interface OutputFormatter {

    /**
     * Formats a list of {@link User} entities into a deterministic string representation.
     */
    String formatUsers(List<User> users);

    /**
     * Formats a list of {@link Document} entities into a deterministic string representation.
     */
    String formatDocuments(List<Document> documents);

    /**
     * Formats a list of {@link WorkflowReport} entities into a deterministic report representation.
     */
    String formatReports(List<WorkflowReport> reports);

    /**
     * Formats a list of {@link WorkflowReport} entities into a deterministic transactions representation.
     */
    String formatTransactions(List<WorkflowReport> transactions);
}
