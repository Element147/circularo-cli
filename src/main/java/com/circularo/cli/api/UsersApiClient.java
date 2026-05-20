package com.circularo.cli.api;

import com.circularo.cli.model.User;
import com.circularo.cli.model.UserSearchResponse;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain-specific API client handling user search operations.
 */
@Component
@RegisterReflectionForBinding({
    User.class,
    UserSearchResponse.class
})
public class UsersApiClient {

    private final BaseCircularoApiClient baseClient;

    @Autowired
    public UsersApiClient(BaseCircularoApiClient baseClient) {
        this.baseClient = baseClient;
    }

    /**
     * Searches for Circularo users using the Elasticsearch syntax query.
     */
    public List<User> searchUsers(String query) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("format", "minimal");
        if (query != null && !query.isBlank()) {
            Map<String, Object> queryMap = new HashMap<>();
            Map<String, Object> filterMap = new HashMap<>();
            Map<String, Object> mustMap = new HashMap<>();
            Map<String, Object> queryStringMap = new HashMap<>();
            queryStringMap.put("query", query);
            mustMap.put("query_string", queryStringMap);
            filterMap.put("must", java.util.List.of(mustMap));
            queryMap.put("query", filterMap);
            payload.put("userQuery", queryMap);
        }
        
        String body = baseClient.toJson(payload);
        HttpRequest request = baseClient.newRequestBuilder("users/search")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
                
        UserSearchResponse searchResponse = baseClient.execute(request, UserSearchResponse.class);
        return searchResponse.results() != null ? searchResponse.results() : java.util.List.of();
    }
}
