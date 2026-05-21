package com.circularo.cli.api;

import com.circularo.cli.model.WorkflowReport;
import com.circularo.cli.model.WorkflowReportItem;
import com.circularo.cli.model.WorkflowReportSearchResponse;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain-specific API client handling workflow report operations.
 */
@Component
@RegisterReflectionForBinding({
    WorkflowReport.class,
    WorkflowReportSearchResponse.class,
    WorkflowReportItem.class
})
public class ReportsApiClient {

    private final BaseCircularoApiClient baseClient;

    @Autowired
    public ReportsApiClient(BaseCircularoApiClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Searches for Circularo document workflow reports using the Elasticsearch syntax query.
     */
    public List<WorkflowReport> searchReports(String query) {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> queryMap = new HashMap<>();
        if (query != null && !query.isBlank()) {
            Map<String, Object> queryStringMap = new HashMap<>();
            queryStringMap.put("query", query);
            Map<String, Object> esQueryMap = new HashMap<>();
            esQueryMap.put("query_string", queryStringMap);
            queryMap.put("esQuery", java.util.List.of(esQueryMap));
        }
        payload.put("query", queryMap);

        String body = baseClient.toJson(payload);
        HttpRequest request = baseClient.newRequestBuilder("search/reports")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        WorkflowReportSearchResponse searchResponse = baseClient.execute(request, WorkflowReportSearchResponse.class);
        return searchResponse.results() != null ? searchResponse.results() : java.util.List.of();
    }
}
