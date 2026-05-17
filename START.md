# Quick Start & Development Guide

This document clearly explains how to build, test, and run the `circularo-cli` application.


## 1. Prerequisites
- The project uses the **Gradle Wrapper** (`gradlew`), meaning you do not need Gradle installed globally on your machine.
- The project is configured with Gradle toolchains, which will **automatically download and provision** the correct Java version (Java 25) and GraalVM distribution for you.

## 2. Building the Application

### Standard JVM Build
To compile the application and build the standard Java JAR:
```powershell
./gradlew build
```

### Native Executable Build (GraalVM)
To compile the application into a high-performance, standalone native executable (`.exe` on Windows):
```powershell
./gradlew nativeCompile
```
*Note: The native compilation process takes a few minutes. The output binary will be located at `build/native/nativeCompile/circularocli.exe`.*

## 3. Running the Application

### Standard JVM (Fastest for Development)
To run the application directly from source during development:
```powershell
./gradlew bootRun
```

### Native Executable (Fastest Startup)
To run the highly optimized native binary (must be built first):
```powershell
./build/native/nativeCompile/circularocli.exe
```

## 4. Testing the Application

### Standard JVM Tests
To run standard unit and integration tests on the JVM:
```powershell
./gradlew test
```

### Native Tests
To verify test compatibility under GraalVM's Closed-World assumption by running the tests as a native image:
```powershell
./gradlew nativeTest
```
