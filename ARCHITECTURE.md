# Architecture Overview

This project is a modular, deterministic CLI search utility for Circularo resources, designed to produce both human-readable and machine-readable outputs.

## Core Technologies
- **Java 25:** The foundational language.
- **Spring Boot 4:** Used for dependency injection and application structuring.
- **Picocli:** For command-line interface parsing and execution.
- **GraalVM Native Image:** For compiling the application into an ultra-fast, standalone executable.

## Architectural Principles
1. **No Local GraalVM Dependency:** Developers should not need to install GraalVM. The Gradle toolchain automatically provisions the correct JDK and GraalVM distribution.
2. **Deterministic Output:** The CLI must produce predictable, parsable output for integration with other scripts and tools.

## Architecture Decision Records (ADRs)
For detailed technical decisions and the rationale behind them, refer to the ADRs:
* [0001 - GraalVM Native Build Configuration](docs/adr/0001-graalvm-native-build-configuration.md)
