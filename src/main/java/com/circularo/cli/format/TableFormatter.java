package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.WorkflowReport;
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

        return renderTable(headers, data);
    }

    @Override
    public String formatTransactions(List<WorkflowReport> transactions) {
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

        return renderTable(headers, data);
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
}
