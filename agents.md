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
