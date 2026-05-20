# 2. Separation of Concerns in REST API Client Layer

Date: 2026-05-20

## Status

Accepted

## Context

The initial CLI client design aggregated all REST API integrations under a single monolithic `CircularoApiClient` class. This monolithic structure mixed core HTTP protocol concerns (such as native `HttpClient` configuration, base URI resolution, OAuth token parameter injection, Jackson deserialization, and global HTTP-to-exception mappings) with domain-specific API endpoint declarations (`searchUsers`, `searchDocuments`, `searchReports`).

As the CLI tool expands to support additional features, commands, and REST resources, this monolithic approach presents several maintenance risks:
1. **Low Cohesion & High Coupling**: The API client class grows larger with each new resource, becoming harder to read and edit.
2. **Brittle Test Setup**: Unit tests for CLI commands require stubbing out a monolithic double class (`StubApiClient`) which accumulates mock data structures for unrelated commands, violating the interface segregation principle.
3. **Impeded Extensibility**: Developers adding new commands are forced to modify the shared `CircularoApiClient` class, leading to merge conflicts and regression risks.

## Decision

We will refactor the API integration layer to establish a clean separation of concerns using composition:

1. **Extract Core API Client Logic**: Introduce `BaseCircularoApiClient.java`. This component will encapsulate the `HttpClient` configuration, `ObjectMapper` configuration, URI normalization, token query parameter authentication, request execution, and global HTTP status mapping to exceptions. It will expose a clean, type-safe generic `execute(HttpRequest request, Class<T> responseClass)` helper method.
2. **Introduce Domain-Specific Client Components**: Implement distinct API clients for each entity concern:
   - `UsersApiClient.java` handles `searchUsers`
   - `DocumentsApiClient.java` handles `searchDocuments`
   - `ReportsApiClient.java` handles `searchReports`
3. **Register Reflection Metadata Modularly**: Place the `@RegisterReflectionForBinding` annotations directly onto the corresponding domain-specific client components. This keeps AOT/GraalVM metadata localized to where it is used.
4. **Update CLI Commands**: Update Picocli command classes to inject only the specific domain client they require via Spring dependency injection.
5. **Split Test Stubs**: Refactor `StubApiClient` into modular, domain-specific stubs (`StubUsersApiClient`, `StubDocumentsApiClient`, `StubReportsApiClient`) to keep tests localized, isolated, and GraalVM AOT compilation compliant.

## Consequences

- **Positive**: High cohesive design. Each component focuses on a single domain area.
- **Positive**: Localized compilation metadata. Reflection configuration for GraalVM native image is partitioned across domain classes.
- **Positive**: Cleaner unit test files. CLI command tests only require stubbing out their corresponding lightweight domain client.
- **Positive**: Scalability. Adding new commands and API endpoints in the future requires creating a new client class without altering existing ones.
- **Negative**: Slight increase in the total number of classes in the codebase.
