# 0008 - Command Line Interface (CLI) Assembly (Phase 4)

## Exact User Prompt

> Now create a plan for phase 4 and once I review it, implement it.

---

## Objective

Assemble the core CLI commands (`users` and `documents`), ensuring deterministic structured and unstructured querying. Implement elegant runtime exception handling using a custom Picocli `IExecutionExceptionHandler`, and correctly deserialize search responses using wrapper Records.

## Proposed Changes

### 1. Custom Picocli Execution Exception Handler

We need to suppress ugly Java stack traces for typical API and connection failures and print concise, actionable errors to `System.err`.

#### [NEW] [CircularoExecutionExceptionHandler.java](../../src/main/java/com/circularo/cli/cli/CircularoExecutionExceptionHandler.java)
- Implement `CommandLine.IExecutionExceptionHandler`.
- Intercept `CircularoException` hierarchy and print messages clearly. Ensure non-zero exit codes.

#### [MODIFY] [CircularoCliApplication.java](../../src/main/java/com/circularo/cli/CircularoCliApplication.java)
- Wire `CircularoExecutionExceptionHandler` into the `CommandLine` instance.

---

### 2. API Response Wrappers

Search endpoints return results inside wrappers (e.g. `{"results": [{"document": {...}}]}`). We will build Jackson-annotated wrapper records to handle deserialization seamlessly.

#### [NEW] [UserSearchResponse.java](../../src/main/java/com/circularo/cli/model/UserSearchResponse.java)
- `@JsonProperty("results") List<User> results`
- `@JsonProperty("total") int total`

#### [NEW] [DocumentSearchResultWrapper.java](../../src/main/java/com/circularo/cli/model/DocumentSearchResultWrapper.java)
- `@JsonProperty("document") Document document`

#### [NEW] [DocumentSearchResponse.java](../../src/main/java/com/circularo/cli/model/DocumentSearchResponse.java)
- `@JsonProperty("results") List<DocumentSearchResultWrapper> results`
- `@JsonProperty("total") int total`

---

### 3. Command Implementations

#### [MODIFY] [UsersCmd.java](../../src/main/java/com/circularo/cli/cli/UsersCmd.java)
- Define `--query` and `--format` Picocli options.
- Inject `CircularoApiClient`, `TableFormatter`, and `JsonFormatter`.
- Build the `requestUsersPostSearch` payload natively:
  - If `--query` is present, wrap it in a `query_string` Elasticsearch query object.
- Execute the request against `/api/v1/users/search`.
- Deserialize into `UserSearchResponse`.
- Print via requested `OutputFormatter`.

#### [MODIFY] [DocumentsCmd.java](../../src/main/java/com/circularo/cli/cli/DocumentsCmd.java)
- Define `--query` and `--format` Picocli options.
- Inject `CircularoApiClient`, `TableFormatter`, and `JsonFormatter`.
- Build the `requestSearchPostSearch` payload natively:
  - If `--query` is present, push it into the `esQuery` array.
- Execute the request against `/api/v1/search`.
- Deserialize into `DocumentSearchResponse` and unwrap `List<Document>`.
- Print via requested `OutputFormatter`.

---

## Verification Plan

### Automated Tests
- Write unit tests for `UsersCmd` and `DocumentsCmd` verifying correct command execution and output formatting dispatch.
- Run `./gradlew test` and `./gradlew nativeTest`.
