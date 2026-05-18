package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
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
