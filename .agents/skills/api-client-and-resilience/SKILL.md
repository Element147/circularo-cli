---
name: api-client-and-resilience
description: Guidance on using the native Java HTTP client, REST API integration, and error mapping for Circularo.
---

# Circularo API Client & Resilience

## 1. Native Java HTTP Client
- Exclusively use `java.net.http.HttpClient` for all REST API integrations.
- **DO NOT** import external libraries like Retrofit, OkHttp, or Apache HTTP client. This keeps the application lightweight and compatible with GraalVM ahead-of-time (AOT) compilation.

## 2. API Contract-Driven Development
- Always use `docs/api-1.yaml` (Swagger/OpenAPI definition) as the absolute source of truth for Circularo REST API endpoints.
- Verify request parameters, paths, query variables, and response structures against the specification before writing client code.

## 3. Resilience
- Wrap all network operations in try-catch blocks handling `IOException` and `InterruptedException`.
- Map HTTP error status codes (4xx and 5xx) to a custom unchecked exception `CircularoException` containing meaningful detail about the API response.
- Set appropriate request timeouts to avoid hanging connections.
