# 0016 - Security Review and Package Upgrades

**User Prompt:**
"Now I need from you to do a security review. Check every package that is used, update it to the latest version and rebuild and run the application to verify it is running correctly."

## Proposed Technical Design/Steps

1. **Verify Current Dependency Vulnerabilities & State**:
   - Check the dependencies currently declared in [build.gradle](file:///c:/Git/circularo-cli/build.gradle).
   - Review each library's latest version on Maven Central and any associated vulnerabilities.

2. **Update Gradle Dependencies in [build.gradle](file:///c:/Git/circularo-cli/build.gradle)**:
   - Upgrade `org.graalvm.buildtools.native` from `0.11.5` to `1.1.0`.
   - Upgrade `info.picocli:picocli-spring-boot-starter` from `4.7.6` to `4.7.7`.
   - Upgrade `info.picocli:picocli` from `4.7.6` to `4.7.7`.
   - Upgrade `info.picocli:picocli-codegen` from `4.7.6` to `4.7.7`.
   - Upgrade `com.fasterxml.jackson.core:jackson-databind` from `2.18.2` to `2.21.3`.

3. **Verify Build & Functionality (JVM Mode)**:
   - Run `./gradlew clean test` to ensure that JVM compilation succeeds and all unit/integration tests pass.
   - Run `./gradlew bootRun --args="users"` to ensure that live execution in JVM mode works correctly.

4. **Verify Build & Functionality (Native AOT compilation)**:
   - Run `./gradlew nativeCompile` to build the GraalVM native binary.
   - Run `./gradlew nativeTest` to verify that unit/integration tests compile and run properly under Native Image.
   - Run the compiled binary `.\build\native\nativeCompile\circularocli.exe users` to verify live execution of the native binary.

## Post-Execution Task Checklist

- [x] Update dependency and plugin versions in [build.gradle](file:///c:/Git/circularo-cli/build.gradle)
- [x] Run `./gradlew clean test` to verify JVM build and test suite
- [x] Run JVM live execution check for Successful API call (`users search`) and Elasticsearch query (`documents -q "definitionType:ext"`)
- [x] Run `./gradlew nativeTest` to verify native image tests
- [x] Run `./gradlew nativeCompile` to build the native binary
- [x] Run Native live execution check for Successful API call (`users search`) and Elasticsearch query (`documents -q "definitionType:ext"`) using compiled binary

