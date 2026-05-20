package com.circularo.cli.api;

import com.circularo.cli.model.Document;
import com.circularo.cli.model.DocumentSearchResponse;
import com.circularo.cli.model.DocumentSearchResultWrapper;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain-specific API client handling document search operations.
 */
@Component
@RegisterReflectionForBinding({
    Document.class,
    DocumentSearchResponse.class,
    DocumentSearchResultWrapper.class
})
public class DocumentsApiClient {

    private final BaseCircularoApiClient baseClient;

    @Autowired
    public DocumentsApiClient(BaseCircularoApiClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Searches for Circularo documents using the Elasticsearch syntax query.
     */
    public List<Document> searchDocuments(String query) {
        Map<String, Object> payload = new HashMap<>();
        if (query != null && !query.isBlank()) {
            Map<String, Object> queryStringMap = new HashMap<>();
            queryStringMap.put("query", query);
            Map<String, Object> esQueryMap = new HashMap<>();
            esQueryMap.put("query_string", queryStringMap);
            payload.put("esQuery", java.util.List.of(esQueryMap));
        }

        String body = baseClient.toJson(payload);
        HttpRequest request = baseClient.newRequestBuilder("search")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        DocumentSearchResponse searchResponse = baseClient.execute(request, DocumentSearchResponse.class);
        if (searchResponse.results() == null) {
            return java.util.List.of();
        }
        return searchResponse.results().stream()
                .map(DocumentSearchResultWrapper::document)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
