# 0007 – Data Models & Deterministic Output Formatting (Phase 3)

## Exact User Prompt

> Now implement phase 3. Again, do a test driven development, it needs to be tested. Once implemented, run all tests run the application, fix all errors, and once all warnings and errors are fixed, do commit and push into main.

---

## Objective

Define clean, stable Java Records for core search resources (Users and Documents) mapped from `docs/api-1.yaml`. Establish an AOT-friendly extensible `OutputFormatter` layer containing highly deterministic `JsonFormatter` and `TableFormatter` implementations, all validated via comprehensive unit tests.

## User Review Required

> [!NOTE]
> We will add the `com.fasterxml.jackson.core:jackson-databind` dependency to the project to provide high-performance JSON serialization and deserialization.
> This dependency will be configured to be fully compatible with GraalVM Native Image compilation.

## Proposed Changes

We will introduce a lightweight JSON parsing framework, define domain Records, establish the formatter contracts, and implement full testing.

### 1. Build Configuration & Decisions

#### [MODIFY] [build.gradle](file:///c:/Git/circularo-cli/build.gradle)
- Add `implementation 'com.fasterxml.jackson.core:jackson-databind:2.18.2'` or similar to enable Jackson deserialization.

#### [MODIFY] [decisions.md](file:///c:/Git/circularo-cli/docs/decisions.md)
- Document the inclusion of Jackson Databind and our extensible AOT-friendly output formatting layer.

---

### 2. Core Search Resource Records

We will create stable records representing the schemas in `docs/api-1.yaml` under `com.circularo.cli.model`.

#### [NEW] [Workflow.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/Workflow.java)
- A record containing `shareStateName`.

#### [NEW] [Document.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/Document.java)
- A record containing `documentId`, `documentTitle`, `type`, `date`, and `Workflow`.

#### [NEW] [User.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/model/User.java)
- A record containing `id`, `fullname`, `mail`, `name`, `phone`, `recordType`, `role`, and `status`.

---

### 3. Deterministic Output Formatter Layer

We will define the formatter interface and implement two distinct styles under `com.circularo.cli.format`.

#### [NEW] [OutputFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/OutputFormatter.java)
- Extensible, AOT-friendly interface without heavy generic reflection:
  ```java
  public interface OutputFormatter {
      String formatUsers(List<User> users);
      String formatDocuments(List<Document> documents);
  }
  ```

#### [NEW] [JsonFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/JsonFormatter.java)
- Implements `OutputFormatter`.
- Uses a configured `ObjectMapper` to print indented, structured JSON.

#### [NEW] [TableFormatter.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/format/TableFormatter.java)
- Implements `OutputFormatter`.
- Dynamically calculates columns width based on the content and renders a clean, professional ASCII table.

---

## Verification Plan

### Automated Tests

We will write unit tests covering:
- Correct mapping and deserialization of JSON responses into `User` and `Document` records.
- Deterministic, structured JSON formatting with `JsonFormatter`.
- Dynamic, aligned human-readable ASCII formatting with `TableFormatter`.

#### [NEW] [JsonFormatterTest.java](file:///c:/Git/circularo-cli/src/test/java/com/circularo/cli/format/JsonFormatterTest.java)
#### [NEW] [TableFormatterTest.java](file:///c:/Git/circularo-cli/src/test/java/com/circularo/cli/format/TableFormatterTest.java)

Verification command:
```powershell
./gradlew test
```
