package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
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
}
