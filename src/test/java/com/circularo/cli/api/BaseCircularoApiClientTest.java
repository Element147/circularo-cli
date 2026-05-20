package com.circularo.cli.api;

import com.circularo.cli.config.ConfigLoader;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class BaseCircularoApiClientTest {

    @Test
    void sendRequest_happyPath_injectsTokenAndBaseUrl() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com/", "my-secret-token");
        StubHttpResponse stubResponse = new StubHttpResponse(200, "{\"status\":\"ok\"}", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("api/v1/users/search")
                .GET()
                .build();

        // Act
        HttpResponse<String> response = apiClient.sendRequest(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"ok\"}", response.body());

        HttpRequest capturedRequest = httpClient.getLastRequest();
        assertNotNull(capturedRequest);
        assertEquals("https://api.circularo.com/api/v1/users/search?token=my-secret-token", capturedRequest.uri().toString());
        assertEquals("GET", capturedRequest.method());
    }

    @Test
    void sendRequest_withExistingQueryParams_appendsTokenWithAmpersand() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "secret");
        StubHttpResponse stubResponse = new StubHttpResponse(200, "ok", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/search?q=test")
                .GET()
                .build();

        // Act
        apiClient.sendRequest(request);

        // Assert
        HttpRequest capturedRequest = httpClient.getLastRequest();
        assertNotNull(capturedRequest);
        assertEquals("https://api.circularo.com/search?q=test&token=secret", capturedRequest.uri().toString());
    }

    @Test
    void sendRequest_http400_throwsCircularoMalformedInputException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpResponse stubResponse = new StubHttpResponse(400, "Bad Request", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoMalformedInputException exception = assertThrows(CircularoMalformedInputException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("400"));
        assertTrue(exception.getMessage().contains("Bad Request"));
    }

    @Test
    void sendRequest_http401_throwsCircularoCredentialsException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpResponse stubResponse = new StubHttpResponse(401, "Unauthorized", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoCredentialsException exception = assertThrows(CircularoCredentialsException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("401"));
        assertTrue(exception.getMessage().contains("Unauthorized"));
    }

    @Test
    void sendRequest_http403_throwsCircularoAuthorizationException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpResponse stubResponse = new StubHttpResponse(403, "Forbidden", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoAuthorizationException exception = assertThrows(CircularoAuthorizationException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("403"));
        assertTrue(exception.getMessage().contains("Forbidden"));
    }

    @Test
    void sendRequest_http5xx_throwsCircularoUpstreamException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpResponse stubResponse = new StubHttpResponse(503, "Service Unavailable", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoUpstreamException exception = assertThrows(CircularoUpstreamException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("503"));
        assertTrue(exception.getMessage().contains("Service Unavailable"));
    }

    @Test
    void sendRequest_httpOtherErrorCode_throwsGeneralCircularoException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpResponse stubResponse = new StubHttpResponse(404, "Not Found", null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoException exception = assertThrows(CircularoException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertEquals(CircularoException.class, exception.getClass());
        assertTrue(exception.getMessage().contains("404"));
        assertTrue(exception.getMessage().contains("Not Found"));
    }

    @Test
    void sendRequest_ioException_wrapsInCircularoException() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpClient httpClient = new StubHttpClient(new IOException("Simulated network timeout"));

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoException exception = assertThrows(CircularoException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("Network error occurred"));
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void sendRequest_interruptedException_restoresStatusAndThrows() {
        // Arrange
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com", "token");
        StubHttpClient httpClient = new StubHttpClient(new InterruptedException("Simulated thread interruption"));

        BaseCircularoApiClient apiClient = new BaseCircularoApiClient(configLoader, httpClient);

        HttpRequest request = apiClient.newRequestBuilder("/endpoint")
                .GET()
                .build();

        // Act & Assert
        CircularoException exception = assertThrows(CircularoException.class, () -> {
            apiClient.sendRequest(request);
        });
        assertTrue(exception.getMessage().contains("interrupted"));
        assertTrue(Thread.currentThread().isInterrupted(), "Interrupted status must be restored on the thread");

        // Clear interrupt flag for subsequent tests in the JVM
        Thread.interrupted();
    }

    // =========================================================================
    // Pure AOT-compliant Manual Stubs for testing
    // =========================================================================

    private static class StubConfigLoader extends ConfigLoader {
        private final String apiUrl;
        private final String apiToken;

        public StubConfigLoader(String apiUrl, String apiToken) {
            this.apiUrl = apiUrl;
            this.apiToken = apiToken;
        }

        @Override
        public String getApiUrl() {
            return apiUrl;
        }

        @Override
        public String getApiToken() {
            if (apiToken == null) {
                throw new CircularoException("API token not configured");
            }
            return apiToken;
        }
    }

    private static class StubHttpResponse implements HttpResponse<String> {
        private final int statusCode;
        private final String body;
        private final HttpRequest request;

        public StubHttpResponse(int statusCode, String body, HttpRequest request) {
            this.statusCode = statusCode;
            this.body = body;
            this.request = request;
        }

        @Override public int statusCode() { return statusCode; }
        @Override public HttpRequest request() { return request; }
        @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (k, v) -> true); }
        @Override public String body() { return body; }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return request != null ? request.uri() : null; }
        @Override public HttpClient.Version version() { return HttpClient.Version.HTTP_2; }
    }

    private static class StubHttpClient extends HttpClient {
        private final HttpResponse<String> stubbedResponse;
        private final IOException ioException;
        private final InterruptedException interruptedException;
        private HttpRequest lastRequest;

        public StubHttpClient(HttpResponse<String> stubbedResponse) {
            this.stubbedResponse = stubbedResponse;
            this.ioException = null;
            this.interruptedException = null;
        }

        public StubHttpClient(IOException ioException) {
            this.stubbedResponse = null;
            this.ioException = ioException;
            this.interruptedException = null;
        }

        public StubHttpClient(InterruptedException interruptedException) {
            this.stubbedResponse = null;
            this.ioException = null;
            this.interruptedException = interruptedException;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            this.lastRequest = request;
            if (ioException != null) throw ioException;
            if (interruptedException != null) throw interruptedException;
            return (HttpResponse<T>) stubbedResponse;
        }

        public HttpRequest getLastRequest() {
            return lastRequest;
        }

        @Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
        @Override public Optional<Duration> connectTimeout() { return Optional.empty(); }
        @Override public Redirect followRedirects() { return Redirect.NEVER; }
        @Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
        @Override public SSLContext sslContext() { return null; }
        @Override public SSLParameters sslParameters() { return new SSLParameters(); }
        @Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
        @Override public Version version() { return Version.HTTP_2; }
        @Override public Optional<Executor> executor() { return Optional.empty(); }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) { return null; }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) { return null; }
    }
}
