# Circularo CLI - Incremental Implementation Plan

**Instructions for the AI Agent:**
This document serves as our source of truth and state tracker for the Circularo CLI implementation. We are building a modular, deterministic CLI search utility. You must read this plan before executing tasks. When you complete a task, you MUST update this file by changing `[ ]` to `[x]` and commit the change to preserve progress history. Work in small steps and verify the result after each meaningful change.

## Phase 1: Scaffolding & Core Architecture (Completed)
- [x] Initialize Git repository, `build.gradle` (Java 25, Spring Boot 3 Non-Web, Picocli, GraalVM).
- [x] Create the thin CLI layer, API layer, config layer, and formatting layer packages.
- [x] Implement `ConfigLoader` with a fallback pattern for secure credential handling.
- [x] Set up GitHub Actions CI pipeline for automated building.
- [x] Establish `docs/decisions.md` to track architectural choices.

## Phase 2: API Client & Error Handling (Day 2)
*Goal: Establish a resilient API communication layer using native Java libraries.*
- [x] Create a `CircularoException` hierarchy in the `api` package. It must distinguish between invalid credentials, authorization failures, malformed input, and upstream API errors.
- [x] Implement `CircularoApiClient` using `java.net.http.HttpClient` to ensure minimal dependencies.
- [x] Implement a generic `sendRequest(HttpRequest)` method that injects the authorization token retrieved from `ConfigLoader`.
- [x] Implement robust error propagation: intercept non-200 HTTP status codes and map them to the specific `CircularoException` variants.
- [x] **Document:** Add a note in `docs/decisions.md` explaining the API client design, error mapping strategy, and how auth tokens are attached.

## Phase 3: Data Models & Output Formatting (Day 3)
*Goal: Ensure deterministic output suitable for both manual inspection and downstream automation.*
- [ ] Define Java Records in the `model` package representing core Circularo resources (e.g., `User`, `Document`). Keep field naming stable.
- [ ] Define an `OutputFormatter` interface in the `format` layer.
- [ ] Implement `JsonFormatter` to satisfy the requirement for a structured, machine-readable format. Use Jackson Databind.
- [ ] Implement `TableFormatter` (or `TextFormatter`) to satisfy the requirement for a human-readable format.
- [ ] Implement `JsonFormatter` to satisfy the requirement for a structured, machine-readable format. Use Jackson Databind.
- [ ] Implement `TableFormatter` (or `TextFormatter`) to satisfy the requirement for a human-readable format.
- [ ] **Document:** Add an entry in `docs/decisions.md` explaining how raw response details are preserved for automation versus how they are summarized for human readability.

## Phase 4: CLI Surface Assembly & Query Translation (Day 4)
*Goal: Assemble the user-facing command tree and wire it to the API.*
- [ ] Implement `UsersCmd` in the `cli` package. Add `--query` and `--format` flags. Example target: `circularo-cli users --query "mary griffin"`.
- [ ] Implement `DocumentsCmd` in the `cli` package. Add `--query` and `--format` flags. Example target: `circularo-cli documents --query "title: Invoice*"`.
- [ ] Implement a custom `IExecutionExceptionHandler` for Picocli to catch `CircularoException`. It must print clean, predictable error messages to `STDERR` without dumping raw Java stack traces to the user.
- [ ] Wire the `CircularoApiClient` and `OutputFormatter` into the command run logic, ensuring the CLI layer remains thin.
- [ ] Verify that repeated runs with the same inputs produce strictly equivalent outputs.

## Phase 5: Testing, Documentation & Final Review (Day 5)
*Goal: Protect main behaviors and finalize delivery artifacts.*
- [ ] Write unit tests for command parsing and dispatch (verifying Picocli flags).
- [ ] Write unit tests for output formatting (verifying JSON and Table outputs are deterministic).
- [ ] Write tests covering request construction and error handling using a mocked HTTP client or web server.
- [ ] Implement at least one end-to-end happy path test covering the full flow.
- [ ] Update the top-level `README.md` to explain the product, the main command model, and how to configure/run it.
- [ ] Create `docs/open_questions.md`. Document explicitly any API behavior that was unclear or tenant-specific, accepted limitations, and assumptions.
- [ ] Add a short summary of lessons learned, unresolved risks, and recommendations for future iterations to `docs/open_questions.md`.
- [ ] Final verification: Run `./gradlew bootJar` and test the compiled executable. Check all pending documentation requirements.