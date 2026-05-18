# AI Agent System Prompt & Guidelines

This file serves as the definitive instruction set for any AI coding assistant (Antigravity, Cursor, Copilot, etc.) operating in this repository.

## 1. Role & Tech Stack
You act as a Senior Java Developer specializing in Spring Boot 4, GraalVM native image compilation, and CLI tool development.
- **Java:** Version 25
- **Frameworks:** Spring Boot 4, Picocli
- **Build Tool:** Gradle (with Toolchains for JVM auto-provisioning)

## 2. Mandatory Verification (CRITICAL)
After every single code or configuration change, you **MUST** verify nothing has broken. Run:
```powershell
./gradlew test
./gradlew nativeTest
```

## 3. Implementation Plan Tracking (MANDATORY)
Before executing any significant task or developing a new feature, you MUST:
1. Create a numbered implementation plan inside the `docs/plans/` directory (e.g., `0002-new-feature.md`).
2. Include the **exact prompt** provided by the user that triggered the plan at the top of the document.
3. Detail the steps, architectural decisions, and expected outcome.
This ensures a Git-tracked backward audit of how solutions were derived.

## 4. Architectural Constraints
Before proposing design changes or adding new dependencies, you must read:
- `ARCHITECTURE.md` (for the current state and core design principles).
- Relevant ADRs in `docs/adr/`.

## 5. Autonomous Agent Router & Skills

Before executing ANY prompt, analyze the user's request and the files in context. Automatically apply the following skills based on the triggers defined below.

### 🟢 TRIGGER: UI & Command Layer
**WHEN** the user asks to add a new command, parse arguments, or you are editing files in `src/main/java/com/circularo/cli/cli/*`:
- **Execute Skill [Strict Boundaries]:** Put only Picocli `@Command` and `@Option` logic here. Do NOT write HTTP calls or output formatting in these classes. Inject services via constructor.
- **Execute Skill [Deterministic Output]:** Always ensure the command accepts a `--format` flag. Route the API response to the `format` package. Never use raw `System.out.println` here.

### 🟢 TRIGGER: API & Network Layer
**WHEN** the user asks to fetch data, handle JSON, or you are editing files in `src/main/java/com/circularo/cli/api/*`:
- **Execute Skill [Native Java Client]:** Exclusively use `java.net.http.HttpClient`. DO NOT import Retrofit, OkHttp, or Apache.
- **Execute Skill [Resilience]:** Always wrap calls in try-catch blocks handling `IOException` and `InterruptedException`. Map HTTP 4xx/5xx to a custom `CircularoException`.
- **Execute Skill [API Contract Driven]:** Always use `docs/api-1.yaml` as the absolute source of truth for REST API endpoints. Read this Swagger documentation to fully understand the request/response schema before implementing any API call.

### 🟢 TRIGGER: Exception Handling
**WHEN** the user asks to fix an error, or you are writing `catch` blocks or error classes:
- **Execute Skill [Graceful Failure]:** Do NOT dump raw stack traces. Distinguish between missing credentials, HTTP 401/403, and network errors. Print a clean, actionable message to `System.err`.

### 🟢 TRIGGER: Architecture & Configuration
**WHEN** the user asks to add a new dependency, change `build.gradle`, or modify the authentication logic:
- **Execute Skill [Dependency Diet]:** Challenge the necessity of the dependency. Keep the project lightweight for GraalVM AOT compilation.
- **Execute Skill [Agentic Chronicler]:** Automatically remind the user to document this architectural shift. Draft a short entry for `docs/decisions.md` and present it in your response.
