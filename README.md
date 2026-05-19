# Circularo CLI Search Utility

A high-performance command-line utility for interacting with the Circularo REST API, built with Spring Boot, Picocli, and GraalVM Native Image.

## Purpose
This tool provides a deterministic, automation-friendly interface to search and retrieve resources (like Users and Documents) from a Circularo tenant. It is designed to be lightweight, fast, and easy to integrate into CI/CD pipelines or local developer workflows.

## Command Model
The CLI uses a resource-centric command model. All commands support a `--format` option for structured or tabular output.
- **`users`**: Search for users (e.g., `circularo-cli users --query "mary griffin"`).
- **`documents`**: Search for documents (e.g., `circularo-cli documents --query "title: Invoice*"`).

Outputs can be formatted as JSON (`--format=json`) for machine readability or as tables (`--format=table`) for human readability.

## Configuration
The CLI requires an API URL and an authentication token. These can be provided via a `.env` file in the working directory:
```env
CIRCULARO_API_URL=https://api.example.circularo.com
CIRCULARO_API_TOKEN=your_secure_token_here
```

## Repository Organization
- **`cli`**: Contains Picocli command definitions and CLI entry points.
- **`api`**: Contains the native `HttpClient` wrapper and typed exception hierarchy for resilient HTTP communication.
- **`format`**: Contains output formatters (`JsonFormatter`, `TableFormatter`) for deterministic console output.
- **`config`**: Manages environment variables and application configuration loading.
- **`model`**: Contains Java Records representing API request and response schemas.

## How to Build and Run

### Option A: Standard JAR (Fast Local Development)
For rapid iteration, compile and run the standard Fat JAR.

```bash
# Build the JAR
./gradlew bootJar

# Run the CLI
java -jar build/libs/circularo-cli-0.0.1-SNAPSHOT.jar
```

### Option B: GraalVM Native Image (High Performance)
For production consumption, compile an AOT native executable. Requires GraalVM toolchain installed.

```bash
# Compile Native Image
./gradlew nativeCompile

# Run the native binary
./build/native/nativeCompile/circularo-cli
```

### Testing
Run tests before committing:
```bash
./gradlew test
./gradlew nativeTest
```
