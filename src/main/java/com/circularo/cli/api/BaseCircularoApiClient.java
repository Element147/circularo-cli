package com.circularo.cli.api;

import com.circularo.cli.config.ConfigLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Encapsulates the core, native Java HTTP mechanics, authentication token injection,
 * request dispatching, error mapping, and JSON utility methods.
 */
@Component
public class BaseCircularoApiClient {

    private final ConfigLoader configLoader;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public BaseCircularoApiClient(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Dedicated constructor for test double injection.
     */
    public BaseCircularoApiClient(ConfigLoader configLoader, HttpClient httpClient) {
        this.configLoader = configLoader;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Helper method to construct a new {@link HttpRequest.Builder} preconfigured with
     * the absolute resolved endpoint URI using the configured API URL.
     */
    public HttpRequest.Builder newRequestBuilder(String path) {
        String baseUrl = configLoader.getApiUrl();
        String uriString = path;
        if (!uriString.startsWith("http://") && !uriString.startsWith("https://")) {
            if (baseUrl.endsWith("/") && uriString.startsWith("/")) {
                uriString = baseUrl + uriString.substring(1);
            } else if (!baseUrl.endsWith("/") && !uriString.startsWith("/")) {
                uriString = baseUrl + "/" + uriString;
            } else {
                uriString = baseUrl + uriString;
            }
        }
        return HttpRequest.newBuilder(URI.create(uriString));
    }

    /**
     * Dispatches an HTTP request, automatically injecting the API token and resolving URIs,
     * and mapping non-2xx status codes to specific custom exceptions.
     */
    public HttpResponse<String> sendRequest(HttpRequest request) {
        HttpRequest authenticatedRequest = authenticateAndResolve(request);
        try {
            HttpResponse<String> response = httpClient.send(authenticatedRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                return response;
            }

            String responseBody = response.body();
            String errorMessage = String.format("API request failed with status code %d. Response: %s", statusCode, responseBody);

            switch (statusCode) {
                case 400 -> throw new CircularoMalformedInputException(errorMessage);
                case 401 -> throw new CircularoCredentialsException(errorMessage);
                case 403 -> throw new CircularoAuthorizationException(errorMessage);
                default -> {
                    if (statusCode >= 500) {
                        throw new CircularoUpstreamException(errorMessage);
                    } else {
                        throw new CircularoException(errorMessage);
                    }
                }
            }
        } catch (IOException e) {
            throw new CircularoException("Network error occurred during Circularo API call: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CircularoException("Circularo API call was interrupted", e);
        }
    }

    /**
     * Helper method to send a request and immediately deserialize the response JSON body
     * into the specified Java type.
     */
    public <T> T execute(HttpRequest request, Class<T> responseType) {
        HttpResponse<String> response = sendRequest(request);
        try {
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException e) {
            throw new CircularoException("Failed to deserialize response payload", e);
        }
    }

    /**
     * Utility method to write an object into JSON string format.
     */
    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new CircularoException("Failed to serialize request payload", e);
        }
    }

    /**
     * Resolves the request URI to absolute form (if relative) and appends the
     * "token" query parameter for authenticated access.
     */
    private HttpRequest authenticateAndResolve(HttpRequest request) {
        URI originalUri = request.uri();
        String baseUrl = configLoader.getApiUrl();
        String apiToken = configLoader.getApiToken();

        String uriString = originalUri.toString();
        if (!uriString.startsWith("http://") && !uriString.startsWith("https://")) {
            if (baseUrl.endsWith("/") && uriString.startsWith("/")) {
                uriString = baseUrl + uriString.substring(1);
            } else if (!baseUrl.endsWith("/") && !uriString.startsWith("/")) {
                uriString = baseUrl + "/" + uriString;
            } else {
                uriString = baseUrl + uriString;
            }
        }

        URI absoluteUri = URI.create(uriString);
        String query = absoluteUri.getQuery();
        String newQuery = (query == null || query.isBlank()) ? "token=" + apiToken : query + "&token=" + apiToken;

        URI authenticatedUri;
        try {
            authenticatedUri = new URI(
                    absoluteUri.getScheme(),
                    absoluteUri.getUserInfo(),
                    absoluteUri.getHost(),
                    absoluteUri.getPort(),
                    absoluteUri.getPath(),
                    newQuery,
                    absoluteUri.getFragment()
            );
        } catch (URISyntaxException e) {
            throw new CircularoException("Failed to construct authenticated URI: " + e.getMessage(), e);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(authenticatedUri)
                .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));

        request.headers().map().forEach((name, values) -> {
            if (!isRestrictedHeader(name)) {
                for (String value : values) {
                    builder.header(name, value);
                }
            }
        });

        request.timeout().ifPresent(builder::timeout);
        builder.expectContinue(request.expectContinue());

        return builder.build();
    }

    private boolean isRestrictedHeader(String name) {
        return name.equalsIgnoreCase("connection")
                || name.equalsIgnoreCase("content-length")
                || name.equalsIgnoreCase("host")
                || name.equalsIgnoreCase("upgrade");
    }
}
