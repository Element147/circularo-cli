# Circularo CLI Search Utility

A high-performance, deterministic command-line utility for searching and managing resources (Users, Documents, Workflow Reports, Transactions) via the Circularo REST API. Built with Spring Boot, Picocli, and GraalVM Native Image.

> [!IMPORTANT]
> **Complete User Manual Available:**
> While this README provides a quickstart and developer-focused setup guide, the full command reference, syntax guides, and troubleshooting steps can be found in the [User Manual](docs/user_manual.md).

---

## 🚀 Quick Start (Build & Test in 2 Minutes)

Follow these simple steps to build the utility and run your first commands from the repository root:

### 1. Configure Credentials
Create a `.env` file in the root of the project (copying `.env.example`) and fill in your Circularo credentials:
```env
CIRCULARO_API_URL=https://app.sandbox.circularo.com/api/v1/
CIRCULARO_API_TOKEN=your_secure_api_token_here
```

### 2. Build the Standalone Native Executable
Compile the application into a standalone native binary (`circularocli.exe` on Windows, or `circularocli` on Linux).
```bash
./gradlew nativeCompile
```
*(Note: The initial native compilation will take a couple of minutes to build and optimize).*

### 3. Run Commands (Using Local Build Paths)
Once compiled, you can run the executable directly using its local path:

#### On Windows (PowerShell):
* **Check version:**
  ```powershell
  .\build\native\nativeCompile\circularocli.exe --version
  ```
* **Search users:**
  ```powershell
  .\build\native\nativeCompile\circularocli.exe users search -q "Lubo"
  ```
* **List documents:**
  ```powershell
  .\build\native\nativeCompile\circularocli.exe documents
  ```

#### On Linux / macOS (Terminal):
* **Check version:**
  ```bash
  ./build/native/nativeCompile/circularocli --version
  ```
* **Search users:**
  ```bash
  ./build/native/nativeCompile/circularocli users search -q "Lubo"
  ```
* **List documents:**
  ```bash
  ./build/native/nativeCompile/circularocli documents
  ```

---

## 💻 Developer Onboarding & Guide

If you are a developer setting up your local workspace, here is the complete set of commands to build, test, and run the application.

### 🛠️ Prerequisites
- **No manual Java or GraalVM installation is required.** The project uses **Gradle Toolchains** to automatically download and provision the correct GraalVM JDK version (Java 25) during the first build execution.

### 🏗️ Build Targets

#### A. Standard JVM JAR (Fast Compilation)
Ideal for standard code updates and rapid local testing.
```bash
# Build the JVM JAR
./gradlew bootJar

# Run using the built JAR
java -jar build/libs/circularocli-0.0.1-SNAPSHOT.jar users search
```

#### B. GraalVM Native Image (High Performance)
Compiles a standalone native executable for instant startup and low memory footprint.
```bash
# Build native binary
./gradlew nativeCompile
```

**Running the Native Binary:**
- **Windows:**
  ```powershell
  .\build\native\nativeCompile\circularocli.exe users search
  ```
- **Linux / macOS:**
  ```bash
  ./build/native/nativeCompile/circularocli users search
  ```

### 🏃 Running in JVM Development Mode
To execute commands directly from source without packing a JAR or compiling a native image:
```bash
./gradlew bootRun --args="users search"
./gradlew bootRun --args="documents -q 'definitionType:ext'"
```

### 🧪 Running Tests
Always verify code correctness and native compilation compatibility before committing:
```bash
# Run standard JVM unit/integration tests
./gradlew test

# Run tests compiled as a native image (verifies AOT/Closed-World assumptions)
./gradlew nativeTest
```

---

## 📦 CI/CD Pre-compiled Binaries (GitHub Actions)

Since GraalVM Native Image compilation requires an environment matching the target OS, the project is configured with a GitHub Actions CI pipeline that compiles and verifies the utility on both Windows and Linux:
- **Windows Native Binary:** compiled on a Windows runner (`circularo-cli-native-windows-latest` containing `circularocli.exe`).
- **Linux Native Binary:** compiled on an Ubuntu runner (`circularo-cli-native-ubuntu-latest` containing `circularocli`).

You can download these binaries directly from the **Artifacts** section of any successful run on the GitHub Actions tab of this repository.

---

## 📂 Repository Structure

- [docs](file:///c:/Git/circularo-cli/docs): Documentation, API contracts, and decisions:
  - [user_manual.md](file:///c:/Git/circularo-cli/docs/user_manual.md): Complete CLI usage guide and detailed command lists.
  - [api-1-full.yaml](file:///c:/Git/circularo-cli/docs/api-1-full.yaml): Full OpenAPI / Swagger specification of the Circularo API.
  - [plans/](file:///c:/Git/circularo-cli/docs/plans): Full implementation plan audit trail (18+ plans).
  - [decisions.md](file:///c:/Git/circularo-cli/docs/decisions.md): Architecture decision records.
  - [open_questions.md](file:///c:/Git/circularo-cli/docs/open_questions.md): Known limitations and open design questions.
- [src/main/java/com/circularo/cli](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli): Source files structured by concern:
  - `cli/`: Picocli command annotations, options, and subcommand groups.
  - `api/`: Resilient `HttpClient` wrapper and entity endpoints.
  - `format/`: Output formatters (Table, JSON, GFM Markdown).
  - `config/`: Authentication/environment configuration loader.
  - `model/`: DTO schemas representing API responses.

---

## 🤖 Agentic Development Workflow

This project was built using a **fully agentic AI development workflow**, where every feature, architectural decision, and bug fix was driven through a structured plan → implement → verify → document loop.

The complete audit trail is recorded in [`docs/plans/`](docs/plans/), covering 18+ implementation cycles — from initial scaffolding to security reviews to final polishing. Each plan contains:
- The triggering user prompt (verbatim)
- The proposed technical design and steps
- A post-execution checklist with completion status

This workflow is a core demonstration of the agentic coding capability evaluated in this project.

---

## ⚠️ Known Limitations

See [`docs/open_questions.md`](docs/open_questions.md) for the full list. Key points:

- **Pagination**: The `--limit` flag controls result set size (default: 50). Full pagination (page-by-page iteration) is not yet implemented.
- **Token Lifecycle**: The CLI assumes a long-lived API token. No OAuth flow or automatic token refresh is included.
- **Elasticsearch Behavior**: The `/users/search` endpoint on some sandbox tenants may reject non-empty query strings due to Elasticsearch parsing restrictions. Use an empty `--query` flag to list all users in those environments.

