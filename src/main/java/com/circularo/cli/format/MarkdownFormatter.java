package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.WorkflowReportItem;
import com.circularo.cli.model.UserModels;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Deterministic Markdown table and report formatter.
 * Renders domain objects in clean GFM (GitHub Flavored Markdown) format.
 */
@Component
public class MarkdownFormatter implements OutputFormatter {

    @Override
    public String formatUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "No users found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("| ID/Email | Full Name | Role | Status | Type |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- |\n");
        for (User user : users) {
            sb.append("| ")
              .append(escapePipe(getOrEmpty(user.id() != null ? user.id() : user.mail()))).append(" | ")
              .append(escapePipe(getOrEmpty(user.fullname()))).append(" | ")
              .append(escapePipe(getOrEmpty(user.role()))).append(" | ")
              .append(escapePipe(getOrEmpty(user.status()))).append(" | ")
              .append(escapePipe(getOrEmpty(user.recordType()))).append(" |\n");
        }
        return sb.toString();
    }

    @Override
    public String formatDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No documents found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("| Document ID | Title | Type | Created Date | Status |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- |\n");
        for (Document doc : documents) {
            String status = doc.workflow() != null ? getOrEmpty(doc.workflow().shareStateName()) : "N/A";
            sb.append("| ")
              .append(escapePipe(getOrEmpty(doc.documentId()))).append(" | ")
              .append(escapePipe(getOrEmpty(doc.documentTitle()))).append(" | ")
              .append(escapePipe(getOrEmpty(doc.type()))).append(" | ")
              .append(escapePipe(getOrEmpty(doc.date()))).append(" | ")
              .append(escapePipe(status)).append(" |\n");
        }
        return sb.toString();
    }

    @Override
    public String formatReports(List<WorkflowReport> reports) {
        return formatReports(reports, false);
    }

    @Override
    public String formatReports(List<WorkflowReport> reports, boolean verbose) {
        if (reports == null || reports.isEmpty()) {
            return "No reports found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("| Document ID | Title | Owner | Sender | Recipients (P/W/T) |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- |\n");
        for (WorkflowReport report : reports) {
            int processed = report.totalProcessed() != null ? report.totalProcessed() : 0;
            int waiting = report.totalWaitingFor() != null ? report.totalWaitingFor() : 0;
            int total = report.totalRecipients() != null ? report.totalRecipients() : 0;
            String pwt = String.format("%d/%d/%d", processed, waiting, total);

            sb.append("| ")
              .append(escapePipe(getOrEmpty(report.documentId()))).append(" | ")
              .append(escapePipe(getOrEmpty(report.documentTitle()))).append(" | ")
              .append(escapePipe(getOrEmpty(report.owner()))).append(" | ")
              .append(escapePipe(getOrEmpty(report.sender()))).append(" | ")
              .append(escapePipe(pwt)).append(" |\n");
        }

        if (verbose) {
            for (WorkflowReport report : reports) {
                sb.append("\n### Workflow Details: ").append(getOrEmpty(report.documentTitle()))
                  .append(" (").append(getOrEmpty(report.documentId())).append(")\n");
                
                sb.append("\n**Processed Actions:**\n");
                if (report.alreadyProcessed() == null || report.alreadyProcessed().isEmpty()) {
                    sb.append("*None*\n");
                } else {
                    for (WorkflowReportItem item : report.alreadyProcessed()) {
                        sb.append("- **").append(getOrEmpty(item.user())).append("** - `")
                          .append(getOrEmpty(item.shareType())).append("` on ")
                          .append(getOrEmpty(item.date()))
                          .append(item.duration() != null ? " (duration: " + item.duration() + "s)" : "")
                          .append("\n");
                    }
                }

                sb.append("\n**Waiting For:**\n");
                if (report.waitingFor() == null || report.waitingFor().isEmpty()) {
                    sb.append("*None*\n");
                } else {
                    for (WorkflowReportItem item : report.waitingFor()) {
                        sb.append("- **").append(getOrEmpty(item.user())).append("** - `")
                          .append(getOrEmpty(item.shareType())).append("` (on turn since ")
                          .append(getOrEmpty(item.dateOfReceivingTheAction())).append(")")
                          .append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String formatTransactions(List<WorkflowReport> transactions) {
        return formatTransactions(transactions, false);
    }

    @Override
    public String formatTransactions(List<WorkflowReport> transactions, boolean verbose) {
        if (transactions == null || transactions.isEmpty()) {
            return "No transactions found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("| Document ID | Title | Start Date | End Date | State | Duration (s) | End Reason |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- |\n");
        for (WorkflowReport tx : transactions) {
            String duration = tx.transactionDuration() != null ? String.valueOf(tx.transactionDuration()) : "";
            sb.append("| ")
              .append(escapePipe(getOrEmpty(tx.documentId()))).append(" | ")
              .append(escapePipe(getOrEmpty(tx.documentTitle()))).append(" | ")
              .append(escapePipe(getOrEmpty(tx.transactionStartDate()))).append(" | ")
              .append(escapePipe(getOrEmpty(tx.transactionEndDate()))).append(" | ")
              .append(escapePipe(getOrEmpty(tx.shareStateName()))).append(" | ")
              .append(escapePipe(duration)).append(" | ")
              .append(escapePipe(getOrEmpty(tx.transactionEndReason()))).append(" |\n");
        }

        if (verbose) {
            for (WorkflowReport report : transactions) {
                sb.append("\n### Transaction Participant Details: ").append(getOrEmpty(report.documentTitle()))
                  .append(" (").append(getOrEmpty(report.documentId())).append(")\n");
                
                sb.append("\n**Processed Actions:**\n");
                if (report.alreadyProcessed() == null || report.alreadyProcessed().isEmpty()) {
                    sb.append("*None*\n");
                } else {
                    for (WorkflowReportItem item : report.alreadyProcessed()) {
                        sb.append("- **").append(getOrEmpty(item.user())).append("** - `")
                          .append(getOrEmpty(item.shareType())).append("` on ")
                          .append(getOrEmpty(item.date()))
                          .append(item.duration() != null ? " (duration: " + item.duration() + "s)" : "")
                          .append("\n");
                    }
                }

                sb.append("\n**Waiting For:**\n");
                if (report.waitingFor() == null || report.waitingFor().isEmpty()) {
                    sb.append("*None*\n");
                } else {
                    for (WorkflowReportItem item : report.waitingFor()) {
                        sb.append("- **").append(getOrEmpty(item.user())).append("** - `")
                          .append(getOrEmpty(item.shareType())).append("` (on turn since ")
                          .append(getOrEmpty(item.dateOfReceivingTheAction())).append(")")
                          .append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    private String getOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String escapePipe(String value) {
        return value.replace("|", "\\|");
    }

    @Override
    public String formatGenericStatus(UserModels.GenericStatus status) {
        if (status == null) return "No status returned.";
        StringBuilder sb = new StringBuilder();
        sb.append("| Status |\n");
        sb.append("| :--- |\n");
        sb.append("| ").append(escapePipe(getOrEmpty(status.status()))).append(" |\n");
        return sb.toString();
    }

    @Override
    public String formatBulkResponse(UserModels.BulkResponse response) {
        if (response == null) return "No response returned.";
        StringBuilder sb = new StringBuilder();
        sb.append("### Bulk Operation Summary\n");
        sb.append("- **Total Count**: ").append(response.count()).append("\n");
        sb.append("- **Errors Encountered**: ").append(response.errors()).append("\n\n");
        
        List<UserModels.BulkResponseItem> items = response.items();
        if (items != null && !items.isEmpty()) {
            sb.append("| Item ID | Status | Message |\n");
            sb.append("| :--- | :--- | :--- |\n");
            for (UserModels.BulkResponseItem item : items) {
                sb.append("| ").append(escapePipe(getOrEmpty(item.id()))).append(" | ")
                  .append(item.status() != null ? item.status() : "").append(" | ")
                  .append(escapePipe(getOrEmpty(item.message()))).append(" |\n");
            }
        } else {
            sb.append("*No item details returned.*\n");
        }
        return sb.toString();
    }

    @Override
    public String formatUserRights(List<UserModels.UserRightItem> rights) {
        if (rights == null || rights.isEmpty()) return "No user rights found.";
        StringBuilder sb = new StringBuilder();
        sb.append("| Right Name | Group | Description |\n");
        sb.append("| :--- | :--- | :--- |\n");
        for (UserModels.UserRightItem item : rights) {
            sb.append("| ").append(escapePipe(getOrEmpty(item.name()))).append(" | ")
              .append(escapePipe(getOrEmpty(item.group()))).append(" | ")
              .append(escapePipe(getOrEmpty(item.description()))).append(" |\n");
        }
        return sb.toString();
    }

    @Override
    public String formatUserActivation(UserModels.UserActivation activation) {
        if (activation == null) return "No activation data.";
        StringBuilder sb = new StringBuilder();
        sb.append("**Account Activated**: ").append(activation.accountActivated()).append("\n\n");
        if (activation.user() != null) {
            sb.append("### User Details\n");
            sb.append(formatUsers(List.of(activation.user())));
        }
        return sb.toString();
    }

    @Override
    public String formatUserResetToken(UserModels.UserResetToken resetToken) {
        if (resetToken == null) return "No reset token data.";
        StringBuilder sb = new StringBuilder();
        sb.append("| Valid | Email | Expires |\n");
        sb.append("| :--- | :--- | :--- |\n");
        sb.append("| ").append(resetToken.valid()).append(" | ")
          .append(escapePipe(getOrEmpty(resetToken.email()))).append(" | ")
          .append(escapePipe(getOrEmpty(resetToken.expires()))).append(" |\n");
        return sb.toString();
    }

    @Override
    public String formatLoginResponse(UserModels.LoginResponse response) {
        if (response == null) return "No login response.";
        StringBuilder sb = new StringBuilder();
        sb.append("**Login / Activation Token**: `").append(getOrEmpty(response.token())).append("`\n\n");
        if (response.user() != null) {
            sb.append("### User Details\n");
            sb.append(formatUsers(List.of(response.user())));
        }
        if (response.rights() != null && !response.rights().isEmpty()) {
            sb.append("\n**Rights**:\n").append(String.join(", ", response.rights())).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String formatUserConfiguration(UserModels.UserConfiguration config) {
        if (config == null) return "No configuration data.";
        StringBuilder sb = new StringBuilder();
        sb.append("| Setting | Value |\n");
        sb.append("| :--- | :--- |\n");
        sb.append("| Language | ").append(escapePipe(getOrEmpty(config.language()))).append(" |\n");
        sb.append("| View | ").append(escapePipe(getOrEmpty(config.view()))).append(" |\n");
        sb.append("| Scheduler Enabled | ").append(config.scheduler()).append(" |\n");
        sb.append("| Navigation Open | ").append(config.navigationOpen()).append(" |\n");
        return sb.toString();
    }

    @Override
    public String formatPasswordStrength(UserModels.PasswordStrength strength) {
        if (strength == null) return "No password strength details.";
        StringBuilder sb = new StringBuilder();
        sb.append("**Password Strength Score**: `").append(strength.score()).append("/4`\n");
        if (strength.warning() != null && !strength.warning().isBlank()) {
            sb.append("**Warning**: ").append(escapePipe(strength.warning())).append("\n");
        }
        if (strength.suggestions() != null && !strength.suggestions().isEmpty()) {
            sb.append("\n**Suggestions**:\n");
            for (String suggestion : strength.suggestions()) {
                sb.append("- ").append(escapePipe(suggestion)).append("\n");
            }
        }
        return sb.toString();
    }
}

