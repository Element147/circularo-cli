---
name: graalvm-and-build-verification
description: Guidance on writing GraalVM native image-friendly Java code and verifying builds.
---

# GraalVM Native Image & Build Verification

## 1. GraalVM AOT Compilation Rules
- Avoid reflection, dynamic proxies, and runtime classloading that are not registered in the GraalVM reflection configuration.
- Use Spring Boot 4's native support (e.g. `@RegisterReflectionForBinding` or GraalVM hint annotations) when registering custom JSON payload classes for serialization.
- Minimize dependencies to prevent bloat in the native binary size.

## 2. Dependency Diet
- Challenge the necessity of any new dependency before adding it to `build.gradle`. Keep libraries minimal for fast ahead-of-time compilation.

## 3. Mandatory Verification
- After making any code changes, verify that the application and tests run correctly in both JVM and native modes:
  ```powershell
  # 1. Run unit/integration tests
  ./gradlew test
  ./gradlew nativeTest

  # 2. Run in JVM mode and test live users API call
  ./gradlew bootRun --args="users"

  # 3. Build native binary and test live native execution
  ./gradlew nativeCompile
  .\build\native\nativeCompile\circularocli.exe users
  ```
- Always document any decisions or changes in dependencies in `docs/decisions.md`.
