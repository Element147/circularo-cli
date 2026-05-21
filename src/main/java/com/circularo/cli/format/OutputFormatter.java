package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.UserModels;
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
     * Formats a list of {@link WorkflowReport} entities with optional verbosity.
     */
    String formatReports(List<WorkflowReport> reports, boolean verbose);

    /**
     * Formats a list of {@link WorkflowReport} entities into a deterministic transactions representation.
     */
    String formatTransactions(List<WorkflowReport> transactions);

    /**
     * Formats a list of {@link WorkflowReport} entities into a deterministic transactions representation with optional verbosity.
     */
    String formatTransactions(List<WorkflowReport> transactions, boolean verbose);

    /**
     * Formats a GenericStatus object.
     */
    String formatGenericStatus(UserModels.GenericStatus status);

    /**
     * Formats a BulkResponse object.
     */
    String formatBulkResponse(UserModels.BulkResponse response);

    /**
     * Formats a list of UserRightItems.
     */
    String formatUserRights(List<UserModels.UserRightItem> rights);

    /**
     * Formats a UserActivation object.
     */
    String formatUserActivation(UserModels.UserActivation activation);

    /**
     * Formats a UserResetToken object.
     */
    String formatUserResetToken(UserModels.UserResetToken resetToken);

    /**
     * Formats a LoginResponse object.
     */
    String formatLoginResponse(UserModels.LoginResponse response);

    /**
     * Formats a UserConfiguration object.
     */
    String formatUserConfiguration(UserModels.UserConfiguration config);

    /**
     * Formats a PasswordStrength object.
     */
    String formatPasswordStrength(UserModels.PasswordStrength strength);
}

