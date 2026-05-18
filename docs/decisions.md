# Architecture Decision Records

## Agentic Workflow
**Context**: We need to rapidly scaffold out this project while maintaining strict adherence to best practices.
**Decision**: Use an AI agent for initial scaffolding, while strictly governing its output via explicit guidelines (AGENTS.md) and manual oversight, particularly around boundaries and concerns.
**Consequences**: Rapid iteration with an auditable trail of decisions. Ensures the agent doesn't bloat the repository with unnecessary features, adhering to clean architecture.

## Tech Stack
**Context**: The CLI must provide robust dependency injection, easy command routing, and deterministic outputs.
**Decision**: Java 25 and Spring Boot 3/4 (Non-web mode) with Picocli.
**Consequences**: The lack of the web context ensures rapid execution and exit. Picocli provides a first-class annotation-driven CLI experience, while Spring provides IoC and structure.

## Dual Distribution Strategy
**Context**: Developers need rapid iteration and evaluation, while downstream consumers need ultra-fast execution times.
**Decision**: Support standard Fat JAR distribution for local development alongside GraalVM Native Image for CI/CD output.
**Consequences**: Allows rapid `./gradlew bootJar` execution locally while providing sub-millisecond cold starts in production via AOT compilation. Requires avoiding reflection heavy, non-AOT compliant libraries.

## Resilient Native API Client & Error Mapping
**Context**: The Circularo CLI must communicate with a REST API securely and reliably without introducing retrofitting/network library bloat, maintaining compatibility with GraalVM AOT compilation.
**Decision**: 
1. Use native `java.net.http.HttpClient` exclusively for zero external dependencies.
2. Abstract and map HTTP status codes to a strongly typed sub-exception hierarchy (`CircularoMalformedInputException` for 400, `CircularoCredentialsException` for 401, `CircularoAuthorizationException` for 403, and `CircularoUpstreamException` for 5xx).
3. Re-inject `token` query parameters dynamically onto absolute URIs inside the generic `sendRequest(HttpRequest)` client layer.
**Consequences**: Fully GraalVM AOT-compliant. High-performance, low-memory footprints. Elegant error handling that maps direct API failures to custom typed exceptions for clean console reporting without dumping trace logs.

## Local `.env` Environment File Resolution
**Context**: Users need a seamless, zero-config method to configure their API key and base URL without exposing sensitive tokens in shell commands or manual directory setups.
**Decision**: Support automatic resolution of a `.env` file containing `CIRCULARO_API_TOKEN` and `CIRCULARO_API_URL` next to the running application executable or in the current working directory, with robust ignoring of default placeholders.
**Consequences**: Extremely convenient developer and operator experience. Placeholders are safely ignored to fall back to other config layers, preventing misconfiguration crashes. Safe and isolated from system-wide environments.

## Lightweight JSON Parser via Jackson Databind
**Context**: We need to serialize/deserialize API request/response payloads to strongly typed domain structures securely and repeatably without introducing retrofitting bloat or breaking GraalVM compatibility.
**Decision**: Import the industry-standard, lightweight `com.fasterxml.jackson.core:jackson-databind` dependency directly to parse response JSON payloads into Java records.
**Consequences**: Completely native/AOT-compliant under GraalVM when utilizing non-reflective Jackson configurations or standard Record structures. High execution speed, type safety, and seamless integration with Spring Boot's built-in paradigms.

## Deterministic, AOT-Friendly Extensible Output Formatting
**Context**: CLI users and automated scripts require clean, predictable console outputs in various formats (structured JSON, clean human-readable ASCII tables) without incurring performance or runtime reflection penalties.
**Decision**: Establish a highly cohesive, non-reflective `OutputFormatter` interface mapping specific domain models to concrete formatter implementations (`JsonFormatter` and `TableFormatter`).
**Consequences**: Avoids complex runtime generic reflection that slows down GraalVM AOT compilation. Ensures output consistency across repeated runs, enables trivial integration of future formats, and guarantees stable machine-readable automation pipelines.

