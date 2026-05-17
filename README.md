# circularo-cli
A modular, deterministic CLI search utility for Circularo resources producing human and machine-readable outputs. Built with Java 25, Spring Boot 3, Picocli, and Gradle using an agentic AI workflow.

## Native Application Guide
This project is configured to seamlessly compile into an ultra-fast, standalone native executable using GraalVM. **No local GraalVM installation is required**; the Gradle toolchain will auto-provision it for you.

### Building the Native Executable
Compile the application directly into native machine code (`.exe` on Windows):
```powershell
./gradlew nativeCompile
```
*Builds take 2-5 minutes. The output binary is located at `build/native/nativeCompile/circularocli.exe`.*

### Running the Native Executable
Run the standalone application instantly (typically ~10-50ms startup time):
```powershell
./build/native/nativeCompile/circularocli.exe
```

### Running Native Tests
Verify compatibility under the GraalVM Closed-World assumption by running the test suite as a native image:
```powershell
./gradlew nativeTest
```

### Architecture & Technical Decisions
For deep dives into technical decisions and configuration details (such as the toolchain setup and workaround for the GraalVM Gradle plugin bug), refer to our Architecture Decision Records:
* [0001 - GraalVM Native Build Configuration](docs/adr/0001-graalvm-native-build-configuration.md)
