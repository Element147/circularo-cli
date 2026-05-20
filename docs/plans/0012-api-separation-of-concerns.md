# Implementation Plan 0012: API Client Separation of Concerns

## Triggering User Prompt
```
The API classes are getting messy. I can see that all cmd classes are calling this CircularoApiClient class for every api call. But I want to have these API classes separated. Like have one class for users like UsersApiClient.java, DocumentsApiClient.java etc. Also make this as a rule, document this decission and create a implementation plan for API classes refactoring for separation of concerns.
```

## Architectural Decisions
1. **Extraction of Core REST Mechanics**: Extract low-level HTTP client setup, OAuth authentication injection, request routing, base URI building, JSON serialization/deserialization, and custom error status mapping into `BaseCircularoApiClient`.
2. **Domain-Specific Client Classes**: Implement separate client classes (`UsersApiClient`, `DocumentsApiClient`, `ReportsApiClient`) mapping 1-to-1 with distinct business domain concerns.
3. **Constructor-based Dependency Injection**: Inject `BaseCircularoApiClient` into domain client classes, and then inject domain client classes directly into CLI command classes.
4. **GraalVM Reflection Binding Modularization**: Move entity reflection registration annotations (`@RegisterReflectionForBinding`) to their respective domain client classes.
5. **AOT-Friendly Unit Test Stubbing**: Replace the unified `StubApiClient` with separate stub classes (`StubUsersApiClient`, `StubDocumentsApiClient`, `StubReportsApiClient`) to maintain compile-time compatibility with GraalVM without relying on reflective mocks.

## Detailed Steps

### Step 1: Create the Base and Domain API Clients
1. Create `BaseCircularoApiClient.java` under `com.circularo.cli.api`.
2. Create `UsersApiClient.java` containing `searchUsers`.
3. Create `DocumentsApiClient.java` containing `searchDocuments`.
4. Create `ReportsApiClient.java` containing `searchReports`.
5. Remove `CircularoApiClient.java`.

### Step 2: Refactor CLI Commands to Inject Domain Clients
1. Refactor `UsersCmd.java` to inject `UsersApiClient`.
2. Refactor `DocumentsCmd.java` to inject `DocumentsApiClient`.
3. Refactor `ReportsCmd.java` and `TransactionsCmd.java` to inject `ReportsApiClient`.

### Step 3: Update Architecture and Rule Documentation
1. Create a detailed Architecture Decision Record `docs/adr/0002-api-separation-of-concerns.md`.
2. Update `docs/decisions.md` to summarize this decision.
3. Update `agents.md` / `RULE[AGENTS.md]` to add the rule:
   - **Execute Skill [Separate API Concerns]:** All new REST API endpoints must be placed in a dedicated `*ApiClient` class representing their entity domain, delegating core HTTP and authentication features to the shared `BaseCircularoApiClient`.

### Step 4: Refactor Test and Stubs Layer
1. Refactor `CircularoApiClientTest.java` to test `BaseCircularoApiClient`'s underlying HTTP/Error functionality.
2. Add new unit tests: `UsersApiClientTest.java`, `DocumentsApiClientTest.java`, `ReportsApiClientTest.java`.
3. Create lightweight domain stubs `StubUsersApiClient`, `StubDocumentsApiClient`, and `StubReportsApiClient` in the test package.
4. Update command tests (`UsersCmdTest`, `DocumentsCmdTest`, `ReportsCmdTest`, `TransactionsCmdTest`) to use these new stubs.

### Step 5: Verification
1. Run local tests: `./gradlew test`
2. Run native tests: `./gradlew nativeTest`
3. Run the CLI in JVM mode: `./gradlew bootRun --args="users"`
4. Compile native binary and execute: `./gradlew nativeCompile` and then run the generated binary.

## Expected Outcome
A clean, modular API layer where domain concerns are strictly separated, while retaining GraalVM AOT compilation compatibility, full unit test coverage, and stable execution.

## Post-Execution Task Checklist

- `[x]` Refactor API Layer Components
  - `[x]` Create `BaseCircularoApiClient.java`
  - `[x]` Create `UsersApiClient.java`
  - `[x]` Create `DocumentsApiClient.java`
  - `[x]` Create `ReportsApiClient.java`
  - `[x]` Delete original `CircularoApiClient.java`
- `[x]` Update Picocli Commands Injection
  - `[x]` Refactor `UsersCmd.java` to inject `UsersApiClient`
  - `[x]` Refactor `DocumentsCmd.java` to inject `DocumentsApiClient`
  - `[x]` Refactor `ReportsCmd.java` to inject `ReportsApiClient`
  - `[x]` Refactor `TransactionsCmd.java` to inject `ReportsApiClient`
- `[x]` Update Architectural Documentation & Rules
  - `[x]` Update `docs/decisions.md` decision log
  - `[x]` Update `agents.md` / `RULE[AGENTS.md]` guidelines
- `[x]` Refactor Test & Stub Layer
  - `[x]` Update `CircularoApiClientTest.java` to test `BaseCircularoApiClient`
  - `[x]` Create `UsersApiClientTest.java`
  - `[x]` Create `DocumentsApiClientTest.java`
  - `[x]` Create `ReportsApiClientTest.java`
  - `[x]` Delete `StubApiClient.java`
  - `[x]` Create domain-specific stubs (`StubUsersApiClient`, `StubDocumentsApiClient`, `StubReportsApiClient`)
  - `[x]` Update command tests to use the new stubs
- `[x]` Verification
  - `[x]` Run local test suite (`./gradlew test`)
  - `[x]` Run native tests (`./gradlew nativeTest`)
  - `[x]` Verify live execution in JVM (`./gradlew bootRun --args="users"`)
  - `[x]` Verify native compilation (`./gradlew nativeCompile`) and binary execution

