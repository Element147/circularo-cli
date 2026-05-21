package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.WorkflowReportItem;
import com.circularo.cli.model.UserModels;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Deterministic human-readable tabular output formatter.
 * Produces clean, professional aligned ASCII tables representing search outputs.
 */
@Component
public class TableFormatter implements OutputFormatter {

    @Override
    public String formatUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "No users found.";
        }

        String[] headers = {"ID/Email", "Full Name", "Role", "Status", "Type"};
        String[][] data = new String[users.size()][5];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = getOrEmpty(user.id() != null ? user.id() : user.mail());
            data[i][1] = getOrEmpty(user.fullname());
            data[i][2] = getOrEmpty(user.role());
            data[i][3] = getOrEmpty(user.status());
            data[i][4] = getOrEmpty(user.recordType());
        }

        return renderTable(headers, data);
    }

    @Override
    public String formatDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "No documents found.";
        }

        String[] headers = {"Document ID", "Title", "Type", "Created Date", "Status"};
        String[][] data = new String[documents.size()][5];

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            data[i][0] = getOrEmpty(doc.documentId());
            data[i][1] = getOrEmpty(doc.documentTitle());
            data[i][2] = getOrEmpty(doc.type());
            data[i][3] = getOrEmpty(doc.date());
            data[i][4] = doc.workflow() != null ? getOrEmpty(doc.workflow().shareStateName()) : "N/A";
        }

        return renderTable(headers, data);
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

        String[] headers = {"Document ID", "Title", "Owner", "Sender", "Recipients (P/W/T)"};
        String[][] data = new String[reports.size()][5];

        for (int i = 0; i < reports.size(); i++) {
            WorkflowReport report = reports.get(i);
            data[i][0] = getOrEmpty(report.documentId());
            data[i][1] = getOrEmpty(report.documentTitle());
            data[i][2] = getOrEmpty(report.owner());
            data[i][3] = getOrEmpty(report.sender());
            
            int processed = report.totalProcessed() != null ? report.totalProcessed() : 0;
            int waiting = report.totalWaitingFor() != null ? report.totalWaitingFor() : 0;
            int total = report.totalRecipients() != null ? report.totalRecipients() : 0;
            data[i][4] = String.format("%d/%d/%d", processed, waiting, total);
        }

        StringBuilder sb = new StringBuilder(renderTable(headers, data));

        if (verbose) {
            for (WorkflowReport report : reports) {
                sb.append("\n=== Workflow Details: ").append(getOrEmpty(report.documentTitle()))
                  .append(" (").append(getOrEmpty(report.documentId())).append(") ===\n");
                
                sb.append("Processed Actions:\n");
                if (report.alreadyProcessed() == null || report.alreadyProcessed().isEmpty()) {
                    sb.append("  (None)\n");
                } else {
                    for (WorkflowReportItem item : report.alreadyProcessed()) {
                        sb.append("  - ").append(getOrEmpty(item.user()))
                          .append(" [").append(getOrEmpty(item.shareType())).append("] on ")
                          .append(getOrEmpty(item.date()))
                          .append(item.duration() != null ? " (duration: " + item.duration() + "s)" : "")
                          .append("\n");
                    }
                }

                sb.append("Waiting For:\n");
                if (report.waitingFor() == null || report.waitingFor().isEmpty()) {
                    sb.append("  (None)\n");
                } else {
                    for (WorkflowReportItem item : report.waitingFor()) {
                        sb.append("  - ").append(getOrEmpty(item.user()))
                          .append(" [").append(getOrEmpty(item.shareType())).append("] on turn since ")
                          .append(getOrEmpty(item.dateOfReceivingTheAction()))
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

        String[] headers = {"Document ID", "Title", "Start Date", "End Date", "State", "Duration (s)", "End Reason"};
        String[][] data = new String[transactions.size()][7];

        for (int i = 0; i < transactions.size(); i++) {
            WorkflowReport tx = transactions.get(i);
            data[i][0] = getOrEmpty(tx.documentId());
            data[i][1] = getOrEmpty(tx.documentTitle());
            data[i][2] = getOrEmpty(tx.transactionStartDate());
            data[i][3] = getOrEmpty(tx.transactionEndDate());
            data[i][4] = getOrEmpty(tx.shareStateName());
            data[i][5] = tx.transactionDuration() != null ? String.valueOf(tx.transactionDuration()) : "";
            data[i][6] = getOrEmpty(tx.transactionEndReason());
        }

        StringBuilder sb = new StringBuilder(renderTable(headers, data));

        if (verbose) {
            for (WorkflowReport report : transactions) {
                sb.append("\n=== Transaction Participant Details: ").append(getOrEmpty(report.documentTitle()))
                  .append(" (").append(getOrEmpty(report.documentId())).append(") ===\n");
                
                sb.append("Processed Actions:\n");
                if (report.alreadyProcessed() == null || report.alreadyProcessed().isEmpty()) {
                    sb.append("  (None)\n");
                } else {
                    for (WorkflowReportItem item : report.alreadyProcessed()) {
                        sb.append("  - ").append(getOrEmpty(item.user()))
                          .append(" [").append(getOrEmpty(item.shareType())).append("] on ")
                          .append(getOrEmpty(item.date()))
                          .append(item.duration() != null ? " (duration: " + item.duration() + "s)" : "")
                          .append("\n");
                    }
                }

                sb.append("Waiting For:\n");
                if (report.waitingFor() == null || report.waitingFor().isEmpty()) {
                    sb.append("  (None)\n");
                } else {
                    for (WorkflowReportItem item : report.waitingFor()) {
                        sb.append("  - ").append(getOrEmpty(item.user()))
                          .append(" [").append(getOrEmpty(item.shareType())).append("] on turn since ")
                          .append(getOrEmpty(item.dateOfReceivingTheAction()))
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

    private String renderTable(String[] headers, String[][] data) {
        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }

        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        // Render border top
        appendBorder(sb, colWidths);

        // Render header
        sb.append("|");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(padRight(headers[i], colWidths[i])).append(" |");
        }
        sb.append("\n");

        // Render border header separator
        appendBorder(sb, colWidths);

        // Render rows
        for (String[] row : data) {
            sb.append("|");
            for (int i = 0; i < row.length; i++) {
                sb.append(" ").append(padRight(row[i], colWidths[i])).append(" |");
            }
            sb.append("\n");
        }

        // Render border bottom
        appendBorder(sb, colWidths);

        return sb.toString();
    }

    private void appendBorder(StringBuilder sb, int[] colWidths) {
        sb.append("+");
        for (int width : colWidths) {
            sb.append("-".repeat(width + 2)).append("+");
        }
        sb.append("\n");
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    @Override
    public String formatGenericStatus(UserModels.GenericStatus status) {
        if (status == null) return "No status returned.";
        return renderTable(new String[]{"Status"}, new String[][]{{getOrEmpty(status.status())}});
    }

    @Override
    public String formatBulkResponse(UserModels.BulkResponse response) {
        if (response == null) return "No response returned.";
        StringBuilder sb = new StringBuilder();
        sb.append("Bulk Operation Summary:\n");
        sb.append("  Total Count: ").append(response.count()).append("\n");
        sb.append("  Errors Encountered: ").append(response.errors()).append("\n\n");
        
        List<UserModels.BulkResponseItem> items = response.items();
        if (items != null && !items.isEmpty()) {
            String[] headers = {"Item ID", "Status", "Message"};
            String[][] data = new String[items.size()][3];
            for (int i = 0; i < items.size(); i++) {
                UserModels.BulkResponseItem item = items.get(i);
                data[i][0] = getOrEmpty(item.id());
                data[i][1] = item.status() != null ? String.valueOf(item.status()) : "";
                data[i][2] = getOrEmpty(item.message());
            }
            sb.append(renderTable(headers, data));
        } else {
            sb.append("No item details returned.");
        }
        return sb.toString();
    }

    @Override
    public String formatUserRights(List<UserModels.UserRightItem> rights) {
        if (rights == null || rights.isEmpty()) return "No user rights found.";
        String[] headers = {"Right Name", "Group", "Description"};
        String[][] data = new String[rights.size()][3];
        for (int i = 0; i < rights.size(); i++) {
            UserModels.UserRightItem item = rights.get(i);
            data[i][0] = getOrEmpty(item.name());
            data[i][1] = getOrEmpty(item.group());
            data[i][2] = getOrEmpty(item.description());
        }
        return renderTable(headers, data);
    }

    @Override
    public String formatUserActivation(UserModels.UserActivation activation) {
        if (activation == null) return "No activation data.";
        StringBuilder sb = new StringBuilder();
        sb.append("Account Activated: ").append(activation.accountActivated()).append("\n\n");
        if (activation.user() != null) {
            sb.append("User Details:\n");
            sb.append(formatUsers(List.of(activation.user())));
        }
        return sb.toString();
    }

    @Override
    public String formatUserResetToken(UserModels.UserResetToken resetToken) {
        if (resetToken == null) return "No reset token data.";
        String[] headers = {"Valid", "Email", "Expires"};
        String[][] data = new String[][]{{
            String.valueOf(resetToken.valid()),
            getOrEmpty(resetToken.email()),
            getOrEmpty(resetToken.expires())
        }};
        return renderTable(headers, data);
    }

    @Override
    public String formatLoginResponse(UserModels.LoginResponse response) {
        if (response == null) return "No login response.";
        StringBuilder sb = new StringBuilder();
        sb.append("Login / Activation Token: ").append(getOrEmpty(response.token())).append("\n\n");
        if (response.user() != null) {
            sb.append("User Details:\n");
            sb.append(formatUsers(List.of(response.user())));
        }
        if (response.rights() != null && !response.rights().isEmpty()) {
            sb.append("\nRights:\n  ").append(String.join(", ", response.rights())).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String formatUserConfiguration(UserModels.UserConfiguration config) {
        if (config == null) return "No configuration data.";
        String[] headers = {"Setting", "Value"};
        String[][] data = new String[][]{
            {"Language", getOrEmpty(config.language())},
            {"View", getOrEmpty(config.view())},
            {"Scheduler Enabled", String.valueOf(config.scheduler())},
            {"Navigation Open", String.valueOf(config.navigationOpen())}
        };
        return renderTable(headers, data);
    }

    @Override
    public String formatPasswordStrength(UserModels.PasswordStrength strength) {
        if (strength == null) return "No password strength details.";
        StringBuilder sb = new StringBuilder();
        sb.append("Password Strength Score: ").append(strength.score()).append("/4\n");
        if (strength.warning() != null && !strength.warning().isBlank()) {
            sb.append("Warning: ").append(strength.warning()).append("\n");
        }
        if (strength.suggestions() != null && !strength.suggestions().isEmpty()) {
            sb.append("Suggestions:\n");
            for (String suggestion : strength.suggestions()) {
                sb.append("  - ").append(suggestion).append("\n");
            }
        }
        return sb.toString();
    }
}

