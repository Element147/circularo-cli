package com.circularo.cli.api;

import com.circularo.cli.config.ConfigLoader;
import com.circularo.cli.model.User;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class UsersApiClientTest {

    @Test
    void searchUsers_returnsUsersList() {
        StubConfigLoader configLoader = new StubConfigLoader("https://api.circularo.com/", "token");
        String jsonResponse = "{\"results\":[{\"id\":\"1\",\"fullname\":\"John Doe\",\"mail\":\"john@example.com\",\"username\":\"johndoe\",\"role\":\"user\",\"status\":\"active\"}]}";
        StubHttpResponse stubResponse = new StubHttpResponse(200, jsonResponse, null);
        StubHttpClient httpClient = new StubHttpClient(stubResponse);

        BaseCircularoApiClient baseClient = new BaseCircularoApiClient(configLoader, httpClient);
        UsersApiClient usersApiClient = new UsersApiClient(baseClient);

        List<User> users = usersApiClient.searchUsers("john", 50);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).fullname());
        assertEquals("john@example.com", users.get(0).mail());

        HttpRequest lastRequest = httpClient.getLastRequest();
        assertNotNull(lastRequest);
        assertTrue(lastRequest.uri().toString().contains("users/search"));
    }

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
        private HttpRequest lastRequest;

        public StubHttpClient(HttpResponse<String> stubbedResponse) {
            this.stubbedResponse = stubbedResponse;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            this.lastRequest = request;
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
