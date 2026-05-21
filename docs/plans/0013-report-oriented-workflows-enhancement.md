# 0013 - Report-Oriented Workflows Enhancement

## Exact User Prompt

> There is stated in the following document: cli-search-utility-spec.pdf that at least one workflow shall be supported. I want to create a complete implementation plan for report-oriented workflows. And once I review it, implement it.

---

## Objective

Enhance the Circularo CLI's report-oriented workflows to fully satisfy the `cli-search-utility-spec.pdf` specification. This includes:
1. Implementing the Markdown (`--format md` / `-f md`) output formatter across all CLI commands (`users`, `documents`, `reports`, `transactions`) to support the documented concrete examples.
2. Deserializing nested workflow participant status arrays (`alreadyProcessed` and `waitingFor`) in the report models.
3. Adding a `--verbose` flag to report-oriented commands (`reports` and `transactions`) to display detailed participant lists (who processed what action, who is currently waiting on turn, etc.) in tabular and markdown formats.
4. Ensuring complete GraalVM reflection registration for all new and modified models to prevent serialization failures in native executable mode.
5. Verifying build, running unit tests, and writing new test coverage.
6. Updating Picocli command annotations and `docs/user_manual.md` as dual sources of truth.

---

## Proposed Changes

### 1. Model Layer

#### [NEW] [WorkflowReportItem.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/WorkflowReportItem.java)
- Map the JSON structure `responseSearchReportsSingleItem` from the OpenAPI specification `docs/api-1.yaml`.
- Include properties: `user`, `shareType`, `date`, `seen`, `duration`, `expiration`, `reminder`, `dateOfReceivingTheAction`, `delegatedUser`, `lastAction`, `order`, `shareId`, `verification`.

#### [MODIFY] [WorkflowReport.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/WorkflowReport.java)
- Add list deserialization properties:
  - `alreadyProcessed`: `List<WorkflowReportItem>`
  - `waitingFor`: `List<WorkflowReportItem>`

---

### 2. API / Registration Layer

#### [MODIFY] [ReportsApiClient.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/api/ReportsApiClient.java)
- Update `@RegisterReflectionForBinding` to register `WorkflowReportItem.class` for reflection under AOT.

#### [MODIFY] [JsonFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/JsonFormatter.java)
- Update `@RegisterReflectionForBinding` to register `WorkflowReportItem.class` for reflection under AOT.

---

### 3. Formatting Layer

#### [NEW] [MarkdownFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/MarkdownFormatter.java)
- Implement `OutputFormatter` to format output models as Markdown tables.
- Support `formatUsers`, `formatDocuments`, `formatReports`, and `formatTransactions`.
- Handle rendering of verbose participant lists when reports/transactions are formatted with detail support.

---

### 4. CLI / Command Layer

#### [MODIFY] [UsersCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/UsersCmd.java)
- Update `--format` option documentation to include `md`.
- Wire `MarkdownFormatter` and execute it when `md` format is selected.

#### [MODIFY] [DocumentsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/DocumentsCmd.java)
- Update `--format` option documentation to include `md`.
- Wire `MarkdownFormatter` and execute it when `md` format is selected.

#### [MODIFY] [ReportsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/ReportsCmd.java)
- Add `--verbose` option to toggle detailed participants listing.
- Update `--format` option documentation to include `md`.
- Wire `MarkdownFormatter` and pass verbose state into formatters.

#### [MODIFY] [TransactionsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/TransactionsCmd.java)
- Add `--verbose` option to toggle detailed participants listing.
- Update `--format` option documentation to include `md`.
- Wire `MarkdownFormatter` and pass verbose state into formatters.

---

### 5. Documentation & Manual

#### [MODIFY] [user_manual.md](file:///c:/Git/circularo-cli/docs/user_manual.md)
- Document the new `--format md` options across all commands.
- Document the `--verbose` flag on `reports` and `transactions` commands.
- Provide usage examples.

---

## Verification Plan

### Automated Tests
- Run Gradle test suite: `./gradlew test`
- Run native test suite: `./gradlew nativeTest`
- Update stubs and command tests:
  - Add mock `alreadyProcessed` and `waitingFor` lists to report stubs in `StubReportsApiClient`.
  - Add unit tests for `MarkdownFormatter`.
  - Update `ReportsCmdTest` and `TransactionsCmdTest` to cover the new `--verbose` and `--format md` options.

### Manual Verification
- Compile native binary: `./gradlew nativeCompile`
- Run the compiled binary to verify outputs:
  - `circularocli.exe users --format md`
  - `circularocli.exe reports --format md`
  - `circularocli.exe reports --verbose`
  - `circularocli.exe transactions --state completed --verbose --format md`

---

## Post-Execution Task Checklist

- `[x]` Implement new models (`WorkflowReportItem`) and update existing `WorkflowReport`
- `[x]` Register reflection metadata for AOT compilation
- `[x]` Implement `MarkdownFormatter` and integrate it into command execution path
- `[x]` Implement `--verbose` options for `ReportsCmd` and `TransactionsCmd`
- `[x]` Update unit tests (stubs, command tests, formatter tests)
- `[x]` Update `docs/user_manual.md` to reflect new capabilities and options
- `[x]` Verify application locally via Gradle JVM tests (`./gradlew test`) and bootRun
- `[x]` Compile and verify under GraalVM native image execution (`./gradlew nativeCompile`, `./gradlew nativeTest`)
