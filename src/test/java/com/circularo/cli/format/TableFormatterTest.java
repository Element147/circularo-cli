package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableFormatterTest {

    private final TableFormatter formatter = new TableFormatter();

    @Test
    void formatUsers_emptyList_returnsFriendlyMessage() {
        String result = formatter.formatUsers(List.of());
        assertEquals("No users found.", result);
    }

    @Test
    void formatUsers_withItems_rendersBeautifulTable() {
        User user1 = new User("usr-1", "John Doe", "john@example.com", "john", "123", "internal", "admin", "active");
        User user2 = new User("usr-2", "Jane Smith", "jane@example.com", "jane", "456", "external", "user", "suspended");

        String result = formatter.formatUsers(List.of(user1, user2));

        assertNotNull(result);
        assertTrue(result.contains("ID/Email"));
        assertTrue(result.contains("Full Name"));
        assertTrue(result.contains("Role"));
        assertTrue(result.contains("Status"));
        assertTrue(result.contains("Type"));
        
        assertTrue(result.contains("usr-1"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("active"));
        assertTrue(result.contains("internal"));
        
        assertTrue(result.contains("usr-2"));
        assertTrue(result.contains("Jane Smith"));
        assertTrue(result.contains("user"));
        assertTrue(result.contains("suspended"));
        assertTrue(result.contains("external"));
        
        // Check grid boundary alignment
        assertTrue(result.contains("+"));
        assertTrue(result.contains("|"));
    }

    @Test
    void formatDocuments_emptyList_returnsFriendlyMessage() {
        String result = formatter.formatDocuments(List.of());
        assertEquals("No documents found.", result);
    }

    @Test
    void formatDocuments_withItems_rendersBeautifulTable() {
        Workflow wf = new Workflow("completed");
        Document doc1 = new Document("doc-1", "Contract", "ext", "2026-05-18", wf);
        Document doc2 = new Document("doc-2", "Invoice Doc", "edoc", "2026-05-19", null);

        String result = formatter.formatDocuments(List.of(doc1, doc2));

        assertNotNull(result);
        assertTrue(result.contains("Document ID"));
        assertTrue(result.contains("Title"));
        assertTrue(result.contains("Type"));
        assertTrue(result.contains("Created Date"));
        assertTrue(result.contains("Status"));

        assertTrue(result.contains("doc-1"));
        assertTrue(result.contains("Contract"));
        assertTrue(result.contains("ext"));
        assertTrue(result.contains("2026-05-18"));
        assertTrue(result.contains("completed"));

        assertTrue(result.contains("doc-2"));
        assertTrue(result.contains("Invoice Doc"));
        assertTrue(result.contains("edoc"));
        assertTrue(result.contains("2026-05-19"));
        assertTrue(result.contains("N/A")); // null workflow maps to N/A
    }
}
