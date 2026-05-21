# 0006 – Resilient API Client & Error Handling (Phase 2)

## Exact User Prompt

> @[IMPLEMENTATION_PLAN.md]
> Now I want you to start with implementation. Continue with the first unchecked task and fully implement the Phase 2 stage. Also make sure, you plan it step by step and carefourly implement it using test driven development.

---

## Objective

Establish a resilient, high-performance API communication layer inside `com.circularo.cli.api` using native Java libraries (`java.net.http.HttpClient`), incorporating a robust typed exception hierarchy, token-injected requests, and comprehensive error mapping.

## User Review Required

> [!NOTE]
> The Circularo API utilizes query-parameter-based authentication (`?token=...`) on its endpoints as specified by `docs/api-1.yaml`.
> We will inject this parameter dynamically in our HTTP request construction.

## Open Questions

None at this stage. The API specification is extremely detailed and clear.

## Proposed Changes

We will modify and create classes strictly within the `com.circularo.cli.api` and `com.circularo.cli.config` packages, verifying everything with test-driven development in `src/test`.

### 1. Exception Hierarchy

We will create specific sub-exceptions of `CircularoException` under the `com.circularo.cli.api` package to represent domain-specific error conditions:

#### [NEW] [CircularoCredentialsException.java](../../src/main/java/com/circularo/cli/api/CircularoCredentialsException.java)
- For invalid, empty, or missing credentials/tokens (mapped to HTTP 401).

#### [NEW] [CircularoAuthorizationException.java](../../src/main/java/com/circularo/cli/api/CircularoAuthorizationException.java)
- For forbidden operations due to lack of rights or permissions (mapped to HTTP 403).

#### [NEW] [CircularoMalformedInputException.java](../../src/main/java/com/circularo/cli/api/CircularoMalformedInputException.java)
- For bad/invalid requests or parsing failures (mapped to HTTP 400).

#### [NEW] [CircularoUpstreamException.java](../../src/main/java/com/circularo/cli/api/CircularoUpstreamException.java)
- For general server errors, downstream service outages, or bad gateway errors (mapped to HTTP 5xx).

---

### 2. ConfigLoader Enhancements

We will extend `ConfigLoader` to support resolving the API base URL in addition to the token.

#### [MODIFY] [ConfigLoader.java](../../src/main/java/com/circularo/cli/config/ConfigLoader.java)
- Introduce `getApiUrl()` to resolve the base URL:
  1. Env var `CIRCULARO_API_URL`
  2. Local properties file `~/.circularo/config.properties`, key `api.url`
  3. Default to `https://api.circularo.com`

---

### 3. API Client Implementation

We will implement a clean, dependency-free API client utilizing `java.net.http.HttpClient`.

#### [NEW] [CircularoApiClient.java](../../src/main/java/com/circularo/cli/api/CircularoApiClient.java)
- Spring-managed `@Component` with constructor injection of `ConfigLoader` and a customizable `HttpClient`.
- A generic method:
  `public HttpResponse<String> sendRequest(HttpRequest request)`
- This method will intercept the request and inject the authentication token query parameter into the URI.
- Intercepts non-200 responses and maps them to the appropriate custom exception.
- Gracefully handles `IOException` and `InterruptedException` by throwing `CircularoException` with clean messages.

---

## Verification Plan

### Automated Tests
We will implement unit and integration tests using JUnit 5 and mock servers to verify all scenarios before completing this phase:
- Test exception mapping (400 -> `CircularoMalformedInputException`, 401 -> `CircularoCredentialsException`, 403 -> `CircularoAuthorizationException`, 500 -> `CircularoUpstreamException`).
- Test request interception and token parameter injection.
- Test fallback behavior when token is missing.

Verification commands:
```powershell
./gradlew test
```
