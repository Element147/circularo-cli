# Circularo CLI - Master Implementation & Specification Plan

**Context for the AI Agent:**
This document is the absolute source of truth for the Circularo CLI project. It replaces any external PDF specifications. We are building a practical CLI utility capable of searching Circularo resources, producing deterministic output, and serving as a solid foundation for future automation work. The emphasis is on judgment, architecture, and execution quality, not on producing the largest possible feature set.

**Agent Workflow Rules:**
1. Work in small steps and verify the result after each meaningful change.
2. Keep notes on unresolved questions and design decisions.
3. For all API endpoints, request structures, and response schemas, strictly refer to the local `api-1.yaml` Swagger file.
4. When you complete a task, update the checkbox from `[ ]` to `[x]` and commit the change to preserve progress history.

---

## Milestone 1: Scaffolding & Core Architecture (Completed)
*Objective: The repository must separate concerns cleanly enough that CLI parsing, API access, query handling, and output formatting do not bleed into one another.*
- [x] Initialize project with Java 25, Spring Boot 3 (Non-Web), Gradle, and Picocli.
- [x] Create modular package structure: `cli`, `api`, `format`, `config`, `model`.
- [x] Implement `ConfigLoader` with a fallback pattern supporting repeatable local execution.
- [x] Set up CI pipeline.
- [x] Establish `docs/decisions.md` to track design choices.

## Milestone 2: API Client & Resilient Error Handling (Completed)
*Objective: The tool should fail clearly and predictably when something goes wrong. The candidate should not hide API failures or collapse them into vague CLI messages.*
- [x] Implement native `java.net.http.HttpClient` wrapper (`CircularoApiClient`).
- [x] Implement token injection from `ConfigLoader` into authorization headers.
- [x] Implement `CircularoException` hierarchy. The CLI must distinguish between:
  - invalid or missing credentials
  - authorization failures
  - malformed input
  - empty result sets
  - upstream API errors
  - local configuration problems
- [x] **Document:** Record API client constraints and error mapping in `docs/decisions.md`.

## Milestone 3: Data Models & Deterministic Output Formatting (Completed)
*Objective: Output must be deterministic. Repeated runs with the same inputs should produce equivalent outputs unless the underlying data has changed.*
- [x] Parse `api-1.yaml` to define Java Records in the `model` package for core search resources (e.g., User, Document). Ensure field naming and ordering remains stable.
- [x] Define an `OutputFormatter` interface in the `format` package. The implementation should avoid ad hoc formatting that makes later automation difficult.
- [x] Implement `JsonFormatter`. The CLI must support at least one structured machine-readable format. Preserve raw response details when useful for downstream processing.
- [x] Implement `TableFormatter` (or `TextFormatter`). The CLI must support at least one tabular or human-readable format.
- [x] **Document:** Record output formatting decisions in `docs/decisions.md`, detailing how raw data is preserved vs. summarized.

## Milestone 4: Command Line Interface (CLI) Assembly (Completed)
*Objective: The CLI should support the core search use case for Circularo data. The behavior should be easy to discover, consistent across resource types, and stable enough to support automation.*
- [x] Implement `UsersCmd` in the `cli` package (e.g., `circularo-cli users --query "mary griffin"`). Delegate strictly to the API layer and use the `OutputFormatter`.
- [x] Implement `DocumentsCmd` in the `cli` package (e.g., `circularo-cli documents --query "title: Invoice*"`). Handle pagination or large-result handling where relevant.
- [x] Ensure commands support structured and unstructured query input where appropriate.
- [x] Implement a custom `IExecutionExceptionHandler` for Picocli. Error messages should help a user or agent understand what failed and where to look next. Do NOT dump raw Java stack traces to the console.
- [x] Ensure the tool can handle large result sets without becoming interactive or unpredictable.

## Milestone 5: Testing Strategy (Completed)
*Objective: The candidate should include tests that prove the CLI behaves as expected for the main workflows. Tests should be targeted and deterministic rather than exhaustive for their own sake.*
- [x] Write tests covering command parsing or dispatch.
- [x] Write tests covering request construction (using mocked HTTP clients or wiremock).
- [x] Write tests covering output formatting.
- [x] Write tests covering error handling.
- [x] Implement at least one end-to-end happy path, if practical.

## Milestone 6: Final Documentation & Delivery Polish
*Objective: The candidate should provide documentation that makes the project usable without a live walkthrough.*
- [ ] Update `README.md` to explain the purpose of the tool, the main command model, how to configure it, and how the repository is organized.
- [ ] Review `docs/decisions.md` to ensure it captures all design choices and limitations intentionally accepted.
- [ ] Create `docs/open_questions.md`. Document any open questions or assumptions that remain, and any API behavior that was unclear or tenant-specific.
- [ ] In `docs/open_questions.md`, add a short summary of lessons learned, a candid note on any struggles, gaps, or unresolved risks, and recommendations for how the project could be improved in a follow-up iteration.
- [ ] Perform a final build (`./gradlew build` and `./gradlew nativeCompile`) and push all commits to the repository.