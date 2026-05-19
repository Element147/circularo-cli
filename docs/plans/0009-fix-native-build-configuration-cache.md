# 0009 - Fix GraalVM Native Build Configuration Cache Error

## Exact User Prompt

> @terminal: Why am not apble to build the native app? Fix it.

---

## Objective

Fix the GraalVM Native image build/test failure under Gradle when Configuration Cache is enabled. Specifically, resolve the `:generateTestResourcesConfigFile` task error that complains about `C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\native-image.cmd wasn't found.`

## Context & Root Cause

1. The project has Gradle Configuration Cache enabled (`org.gradle.configuration-cache=true` in `gradle.properties`).
2. During the serialization of the Gradle configuration cache for native tasks (like `:generateTestResourcesConfigFile`), the GraalVM Gradle plugin attempts to query the path of the GraalVM compiler.
3. By default, GraalVM plugin has `toolchainDetection` set to `true`. This automatic detection checks the host JVM (which is `Eclipse Adoptium JDK 25`) and fails because it is not a GraalVM JDK containing `native-image.cmd`.
4. Since we already explicitly bind the `javaLauncher` to GraalVM for both the `main` and `test` binaries, we do not need the plugin to perform automatic toolchain detection.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle](file:///c:/Git/circularo-cli/build.gradle)
- Add `toolchainDetection = false` to the `graalvmNative` block. This prevents the plugin from executing eager toolchain searches on the host JVM during task graph construction and configuration caching.

```groovy
graalvmNative {
	toolchainDetection = false
	binaries {
		main {
			javaLauncher = javaToolchains.launcherFor {
				languageVersion = JavaLanguageVersion.of(25)
				vendor = JvmVendorSpec.GRAAL_VM
			}
		}
		test {
			javaLauncher = javaToolchains.launcherFor {
				languageVersion = JavaLanguageVersion.of(25)
				vendor = JvmVendorSpec.GRAAL_VM
			}
		}
	}
}
```

---

## Verification Plan

### Automated Tests
- Run `./gradlew test` to ensure standard compilation and tests still function.
- Run `./gradlew nativeTest` with configuration caching enabled to verify the fix works and native tests compile and run successfully.
- Run `./gradlew nativeCompile` to verify native compilation generates the executable.
