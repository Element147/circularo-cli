package com.circularo.cli.format;

import com.circularo.cli.model.User;
import com.circularo.cli.model.Document;
import com.circularo.cli.model.Workflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Deterministic JSON output formatter.
 * Produces structured, machine-readable, pretty-printed representations of the domain models.
 */
@Component
@RegisterReflectionForBinding({User.class, Document.class, Workflow.class})
public class JsonFormatter implements OutputFormatter {

    private final ObjectMapper objectMapper;

    public JsonFormatter() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String formatUsers(List<User> users) {
        try {
            return objectMapper.writeValueAsString(users);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize users to JSON", e);
        }
    }

    @Override
    public String formatDocuments(List<Document> documents) {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize documents to JSON", e);
        }
    }
}
