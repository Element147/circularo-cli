package com.circularo.cli.api;

import com.circularo.cli.config.ConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Resilient, dependency-free API client for Circularo REST endpoints.
 * Uses native Java {@link HttpClient} to preserve performance, lightness,
 * and seamless GraalVM AOT compilation.
 */
@Component
public class CircularoApiClient {

    private final ConfigLoader configLoader;
    private final HttpClient httpClient;

    @Autowired
    public CircularoApiClient(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Package-private constructor dedicated for test injection (TDD mocking).
     */
    CircularoApiClient(ConfigLoader configLoader, HttpClient httpClient) {
        this.configLoader = configLoader;
        this.httpClient = httpClient;
    }

    /**
     * Helper method to construct a new {@link HttpRequest.Builder} preconfigured with
     * the absolute resolved endpoint URI using the configured API URL.
     *
     * @param path the relative path (e.g. "api/v1/users/search" or "/search")
     * @return a request builder configured with the absolute URI
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
     * Authenticates, resolves, and dispatches an HTTP request to Circularo.
     * Maps non-2xx status codes to specific custom exceptions.
     *
     * @param request the base HTTP request (relative or absolute, without credentials)
     * @return the successful HTTP response
     * @throws CircularoException if a network, authorization, validation, or server error occurs
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
