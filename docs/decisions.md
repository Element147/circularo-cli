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
