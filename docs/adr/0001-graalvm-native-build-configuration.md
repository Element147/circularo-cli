# 1. Configuration of GraalVM Native Image Build Toolchain

Date: 2026-05-17

## Status
Accepted

## Context
The project requires building a highly optimized native executable using GraalVM via the Spring Boot Native Build Tools plugin (`org.graalvm.buildtools.native`). 
During the initial build configuration, two significant friction points were encountered:
1. **Local JVM Dependency:** The `nativeCompile` task conventionally requires developers to manually install GraalVM and configure it as their `JAVA_HOME`. Relying on local, out-of-band system configurations degrades developer experience and leads to non-reproducible builds across different machines and CI/CD environments.
2. **`nativeTest` Execution Bug:** The GraalVM Gradle plugin (v0.11.5) fails to execute native tests when the host JVM running Gradle is not GraalVM. The internal `NativeImageExecutableLocator` attempts to resolve a lazy `runtimeArgs` convention provider using the host's default JVM instead of the toolchain, resulting in an immediate crash (`native-image.cmd wasn't found`).

## Decision
We decided to completely decouple the Native Image compilation from the host JVM by utilizing Gradle Toolchain auto-provisioning, ensuring absolute portability.

1. **Gradle Toolchain Auto-Provisioning**: We introduced the `org.gradle.toolchains.foojay-resolver-convention` (v1.0.0) plugin in `settings.gradle`. This enables Gradle to seamlessly download and cache the precise GraalVM JDK required for compilation on any machine automatically.
2. **GraalVM Toolchain Binding**: In `build.gradle`, we explicitly configured both the `java` base extension and the `graalvmNative` main/test binaries to target `JvmVendorSpec.matching("GraalVM")`.
3. **Plugin Bug Workaround**: To resolve the `:nativeTest` execution crash, we explicitly override the faulty convention provider by assigning an empty list directly to the task (`tasks.named('nativeTest') { runtimeArgs.set([]) }`). This bypasses the bug without compromising native test execution.

## Consequences
- **Positive:** Zero-friction onboarding. Developers and CI pipelines can clone the repository and run `./gradlew nativeCompile` immediately without manually provisioning GraalVM.
- **Positive:** The workaround guarantees that `nativeTest` functions correctly across all OS platforms regardless of the host JVM.
- **Negative:** The very first build on a new machine incurs a one-time network latency penalty while Gradle downloads the GraalVM toolchain (~300MB) to the local cache.
