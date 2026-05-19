# Open Questions & Lessons Learned

## Open Questions & Assumptions
- **Pagination**: The API documentation implies support for pagination, but the exact limits and default page sizes might vary by tenant. The CLI currently assumes standard behavior, but edge cases with extremely large result sets might require explicit limit parameters.
- **Token Lifecycle**: It is assumed that the provided `CIRCULARO_API_TOKEN` is long-lived or managed externally by the user. There is no built-in token rotation or Oauth flow in the CLI yet.
- **Tenant-Specific URLs**: The base URL format can vary. We assume the user provides the full base path in `CIRCULARO_API_URL` up to the API version prefix (e.g., `https://api.circularo.com/v1`).

## Lessons Learned
- **GraalVM & Spring Boot**: Building a non-web Spring Boot application with GraalVM native image requires careful dependency management. Using `java.net.http.HttpClient` exclusively helped avoid massive reflection and proxy configurations required by libraries like Retrofit or WebClient.
- **Error Handling**: Mapping raw HTTP errors to a typed exception hierarchy (`CircularoException`) significantly improved the UX of the CLI, allowing for clean error messages instead of raw stack traces.

## Struggles, Gaps, and Unresolved Risks
- **Testing Native Images**: Running tests within the native image context (`nativeTest`) can be slower and more brittle than standard JVM tests. Ensuring all serialization paths are AOT-compliant required strict usage of Jackson annotations.
- **Formatting Complex Data**: The `TableFormatter` provides a good baseline, but flattening highly nested JSON structures from the Circularo API for console display remains challenging. Some nested arrays or objects are currently summarized or omitted in the tabular view.

## Recommendations for Future Iterations
1. **Interactive Mode / Auto-completion**: Add shell auto-completion scripts generation using Picocli's built-in tools.
2. **Integration Tests**: Implement WireMock-based integration tests to validate the API client against mocked API responses during the CI build.
3. **Advanced Filtering**: Expose more advanced querying options (like date ranges or status filters) directly as CLI arguments rather than relying solely on the raw `--query` string.
4. **Configuration Profiles**: Support multiple environments (e.g., `staging`, `production`) in the config loader to easily switch contexts.
