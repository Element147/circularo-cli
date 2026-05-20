# 0011 - Complete Search and Reports/Transactions Workflows

## Exact User Prompt

> V korenove slozce mam soubor se zadanim, co vsechno musi aplikace obsahovat a co by mela splnovat. Je to v souboru cli-search-utility-spec.pdf. Udelej mi review celeho repozitare a porovnej s pozadavky, zdali je vse splneno. Pokud ne, navrhni implementacni plan, podle ktereho tenhle projekt dotahneme do konce.

And the follow-up prompt:

> /goal Implementuj to podle implementacniho planu dokud nebude kompletne hotovy. Zaroven nezapomen a dodrzuj pravidlo, kdy vzdy kdy se navrhne implementacni plan, zadokumentuj promt i implementacni plan ktery byl vygenerovan. Potrebuji trackovat progres. Pokracuj podle schematu, ktery uz je ve slozce docs\plans

---

## Objective

Review the Circularo CLI repository against the `cli-search-utility-spec.pdf` specification and implement the missing functional requirements: the `reports` and `transactions` subcommands, their corresponding formatting logic (JSON and Tabular formats), and update the user manual.

---

## Proposed Changes

### 1. Model Layer

Create new Jackson-annotated data models for the reports endpoint responses.

#### [NEW] [WorkflowReport.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/WorkflowReport.java)
- Mapping to `responseSearchReportsSingle` schema.
- Fields for document metadata, recipient statistics, and transaction details.

#### [NEW] [WorkflowReportSearchResponse.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/WorkflowReportSearchResponse.java)
- Mapping to `responseSearchReportsMultiple` schema wrapper.
- Fields: `results` (List of `WorkflowReport`), `total` (count).

---

### 2. API Layer

#### [MODIFY] [CircularoApiClient.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/api/CircularoApiClient.java)
- Register the new model classes for GraalVM AOT native reflection.
- Add `searchReports(String query)` to query `/search/reports` endpoint via HTTP POST.

---

### 3. Formatting Layer

#### [MODIFY] [OutputFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/OutputFormatter.java)
- Declare `formatReports` and `formatTransactions` methods.

#### [MODIFY] [TableFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/TableFormatter.java)
- Implement `formatReports` rendering fields: `Document ID`, `Title`, `Owner`, `Sender`, `Processed/Waiting/Total`.
- Implement `formatTransactions` rendering fields: `Document ID`, `Title`, `Start Date`, `End Date`, `State`, `Duration (s)`, `End Reason`.

#### [MODIFY] [JsonFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/JsonFormatter.java)
- Implement `formatReports` and `formatTransactions` using Jackson object mapping.

---

### 4. CLI / Command Layer

#### [NEW] [ReportsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/ReportsCmd.java)
- Picocli sub-command for `reports` with `--query` and `--format` options.

#### [NEW] [TransactionsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/TransactionsCmd.java)
- Picocli sub-command for `transactions` with `--query`, `--state`, and `--format` options.
- Includes in-memory filtering by the `--state` parameter.

#### [MODIFY] [RootCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/RootCmd.java)
- Register `ReportsCmd` and `TransactionsCmd` as subcommands.

---

### 5. Documentation & Verification

#### [MODIFY] [user_manual.md](file:///c:/Git/circularo-cli/docs/user_manual.md)
- Document the new subcommands, flags, and usage examples.

---

## Verification Plan

### Automated Tests
- Extend `StubApiClient` to stub report search responses.
- Write unit tests `ReportsCmdTest` and `TransactionsCmdTest`.
- Run `./gradlew test` and `./gradlew nativeTest`.

### Manual Verification
- Compile native executable: `./gradlew nativeCompile`.
- Run subcommands with `--help` and verify options.
- Run with active configuration to verify live data rendering.
