# 0004 - Fix GitHub Workflow Native Build

## Triggering Prompt
> My github workflow is failing. Investigate it and fix it.
> Execution failed for task ':nativeCompile'.
> A problem occurred starting process 'command '/home/runner/.gradle/jdks/graalvm_community-25-amd64-linux.2/bin/native-image''

## Plan & Steps
1. **Root Cause Analysis**: The failure in GitHub Actions happens because Gradle is using the Foojay resolver to download a GraalVM Toolchain (`graalvm_community-25-amd64-linux.2`). This specific downloaded toolchain appears to be either missing the `native-image` binary or missing correct execution permissions on the Ubuntu runner.
2. **Switch Action Setup**: Replace `actions/setup-java@v4` with `graalvm/setup-graalvm@v1` in `.github/workflows/ci.yml`. This official GraalVM action properly sets up the `GRAALVM_HOME` environment, adds `native-image` to the PATH, and automatically integrates with Gradle toolchains so that Gradle doesn't attempt to download its own broken version.
3. **Refine Gradle Configuration (Optional but Recommended)**: Ensure `graalvmNative` uses toolchain detection appropriately without forcing a re-download.

## Architectural Decisions
- We adopt the official `graalvm/setup-graalvm` action over the generic `actions/setup-java` because it provides better integration for native-image builds, effectively eliminating the need for Gradle to redundantly download a flawed GraalVM distribution via Foojay.

## Expected Outcome
The GitHub Actions workflow will successfully compile standard Fat JARs and platform-native executables (`.exe` on Windows, standard ELF binary on Linux) using the provided GraalVM installation, without failing to launch `native-image`.
