package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.Workflow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonFormatterTest {

    private final JsonFormatter formatter = new JsonFormatter();

    @Test
    void formatUsers_emptyList_returnsEmptyJsonArray() {
        String result = formatter.formatUsers(List.of());
        assertEquals("[ ]", result.trim());
    }

    @Test
    void formatUsers_withItems_returnsDeterministicPrettyPrintedJson() {
        User user1 = new User("1", "John Doe", "john@example.com", "john", "123", "internal", "admin", "active");
        User user2 = new User("2", "Jane Smith", "jane@example.com", "jane", null, "external", "user", "suspended");

        String result = formatter.formatUsers(List.of(user1, user2));

        assertNotNull(result);
        assertTrue(result.contains("\"id\" : \"1\""));
        assertTrue(result.contains("\"fullname\" : \"John Doe\""));
        assertTrue(result.contains("\"mail\" : \"john@example.com\""));
        assertTrue(result.contains("\"name\" : \"john\""));
        assertTrue(result.contains("\"phone\" : \"123\""));
        assertTrue(result.contains("\"recordType\" : \"internal\""));
        assertTrue(result.contains("\"role\" : \"admin\""));
        assertTrue(result.contains("\"status\" : \"active\""));
        
        assertTrue(result.contains("\"id\" : \"2\""));
        assertTrue(result.contains("\"fullname\" : \"Jane Smith\""));
        assertTrue(result.contains("\"recordType\" : \"external\""));
        assertTrue(result.contains("\"status\" : \"suspended\""));
    }

    @Test
    void formatDocuments_emptyList_returnsEmptyJsonArray() {
        String result = formatter.formatDocuments(List.of());
        assertEquals("[ ]", result.trim());
    }

    @Test
    void formatDocuments_withItems_returnsDeterministicPrettyPrintedJson() {
        Workflow wf = new Workflow("inprogress");
        Document doc = new Document("doc-123", "Invoice Agreement", "ext", "2026-05-18T12:00:00Z", wf);

        String result = formatter.formatDocuments(List.of(doc));

        assertNotNull(result);
        assertTrue(result.contains("\"documentId\" : \"doc-123\""));
        assertTrue(result.contains("\"documentTitle\" : \"Invoice Agreement\""));
        assertTrue(result.contains("\"type\" : \"ext\""));
        assertTrue(result.contains("\"date\" : \"2026-05-18T12:00:00Z\""));
        assertTrue(result.contains("\"shareStateName\" : \"inprogress\""));
    }
}
