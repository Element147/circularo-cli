# Circularo CLI Search Utility

A high-performance command-line utility for interacting with the Circularo REST API, built with Spring Boot, Picocli, and GraalVM Native Image.

## Architecture
- **CLI Layer**: Picocli annotations for parsing.
- **Config**: Spring Boot application properties (non-web mode).
- **Distribution**: Standard Fat JAR + GraalVM Native Image.

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
