# Architecture Overview

This project is a modular, deterministic CLI search utility for Circularo resources,
designed to produce both human-readable and machine-readable outputs across all major
data domains: users, documents, workflow reports, and transactions.

## Core Technologies

| Technology | Version | Role |
|---|---|---|
| Java | 25 | Primary language |
| Spring Boot | 4 | Dependency injection, lifecycle, non-web mode |
| Picocli | 4.7.7 | CLI annotation-driven command/option parsing |
| GraalVM Native Image | via Gradle toolchain | AOT compilation to native binary |
| Jackson Databind | 2.18.3+ | JSON serialization / deserialization |

> **No local GraalVM required.** The Gradle toolchain plugin automatically provisions
> the correct JVM and GraalVM distribution at build time via toolchain resolution.

---

## Layered Package Architecture

```
src/main/java/com/circularo/cli/
│
├── CircularoCliApplication.java     Entry point — wires Picocli into Spring Boot lifecycle
├── RootCmd.java                     Root @Command — dispatches to domain sub-commands
│
├── cli/                             ── COMMAND LAYER ──────────────────────────────────────
│   ├── UsersCmd.java                  Dispatch group: users
│   ├── UsersSearchCmd.java            users search --query --limit --format
│   ├── UsersGetCmd.java               users get <id>
│   ├── UsersCreateCmd.java            users create --file
│   ├── UsersUpdateCmd.java            users update --file
│   ├── UsersUpdateSingleCmd.java      users update-single <id> --file
│   ├── UsersDeleteCmd.java            users delete <id> [--reassign]
│   ├── UsersGetByUsernameCmd.java     users get-by-username <name>
│   ├── UsersRightsCmd.java            users rights <id>
│   ├── UsersActivateCmd.java          users activate --password --fullname [--file]
│   ├── UsersResendActivationCmd.java  users resend-activation <id>
│   ├── UsersCheckActivationCmd.java   users check-activation
│   ├── UsersChangePasswordCmd.java    users change-password <id>
│   ├── UsersCheckPasswordCmd.java     users check-password --password --tenant
│   ├── UsersResetPasswordCmd.java     users reset-password --username --code --new-password
│   ├── UsersResetPasswordUsernameCmd  users reset-password-username <username>
│   ├── UsersCheckResetTokenCmd.java   users check-reset-token --token
│   ├── UsersAddGroupCmd.java          users add-group <id> --group <name>
│   ├── UsersRemoveGroupCmd.java       users remove-group <id> --group <name>
│   ├── UsersUploadPhotoCmd.java       users upload-photo <id> --file
│   ├── UsersDeletePhotoCmd.java       users delete-photo <id>
│   ├── UsersDeleteSignatureCmd.java   users delete-signature <id>
│   ├── UsersDeleteInitialsCmd.java    users delete-initials <id>
│   ├── UsersAddCertificateCmd.java    users add-certificate <id> --name --type --file
│   ├── UsersRemoveCertificateCmd.java users remove-certificate <id> --cert-id
│   ├── UsersRegisterCmd.java          users register --name --email --fullname
│   ├── DocumentsCmd.java              documents --query --limit --format
│   ├── ReportsCmd.java                reports --query --limit --format --verbose
│   ├── TransactionsCmd.java           transactions --query --state --limit --format --verbose
│   └── CircularoExecutionExceptionHandler  Maps exceptions to clean stderr + exit codes
│
├── api/                             ── API LAYER ───────────────────────────────────────────
│   ├── BaseCircularoApiClient.java    Core HTTP client — auth injection, error mapping, JSON
│   ├── UsersApiClient.java            /users/*, /users/search endpoints
│   ├── DocumentsApiClient.java        /search endpoint
│   ├── ReportsApiClient.java          /search/reports endpoint
│   ├── CircularoException.java        Base exception
│   ├── CircularoCredentialsException  401 Unauthorized
│   ├── CircularoAuthorizationException 403 Forbidden
│   ├── CircularoMalformedInputException 400 Bad Request
│   └── CircularoUpstreamException     5xx Server Error
│
├── model/                           ── MODEL LAYER ─────────────────────────────────────────
│   ├── User.java                      User domain record
│   ├── UserModels.java                Nested user request/response types
│   ├── Document.java                  Document domain record
│   ├── DocumentSearchResponse.java    Search response wrapper
│   ├── DocumentSearchResultWrapper.java
│   ├── WorkflowReport.java            Workflow report / transaction record
│   ├── WorkflowReportItem.java        Individual workflow report action item
│   └── WorkflowReportSearchResponse.java
│
├── format/                          ── FORMAT LAYER ─────────────────────────────────────────
│   ├── OutputFormatter.java           Interface: all format strategies
│   ├── TableFormatter.java            ASCII bordered table output  (--format table)
│   ├── JsonFormatter.java             Indented JSON output         (--format json)
│   └── MarkdownFormatter.java         GitHub Flavored Markdown     (--format md)
│
└── config/                          ── CONFIG LAYER ─────────────────────────────────────────
    └── ConfigLoader.java              3-level fallback: .env → env var → ~/.circularo/
```

### Layer Dependency Rules
```
cli/ → api/ (inject domain ApiClient)
cli/ → format/ (inject OutputFormatter implementations)
api/ → config/ (inject ConfigLoader for credentials)
api/ → model/ (deserialize responses into domain records)
format/ → model/ (render domain records to strings)
```

> The command layer **never** makes HTTP calls directly.
> The API layer **never** formats output.
> Each layer is independently unit-testable via stubs.

---

## Output Format System

Every command accepts a `--format` flag selecting one of three deterministic renderers:

| Flag | Implementation | Use Case |
|---|---|---|
| `--format table` | `TableFormatter` | Human reading in terminal (default) |
| `--format json` | `JsonFormatter` | Piping to `jq`, scripting, CI/CD |
| `--format md` | `MarkdownFormatter` | Documentation, GitHub Markdown tables |

---

## Configuration / Credential Resolution

Credentials are resolved using a **3-level priority fallback chain**:

```
1. .env file (next to the running binary, or in CWD)
   CIRCULARO_API_TOKEN=...
   CIRCULARO_API_URL=...

2. System environment variables
   CIRCULARO_API_TOKEN=...
   CIRCULARO_API_URL=...

3. ~/.circularo/config.properties
   api.token=...
   api.url=...
```

---

## Distribution Strategy

| Artifact | Command | Use Case |
|---|---|---|
| Fat JAR | `./gradlew bootJar` | Local development, rapid iteration |
| GraalVM Native Binary | `./gradlew nativeCompile` | Production, CI/CD, sub-ms startup |
| Native Test | `./gradlew nativeTest` | Full test suite under native image |

---

## Error Handling & Exit Codes

| Exit Code | Meaning |
|---|---|
| `0` | Success |
| `1` | Known business error (auth failure, bad input, API error) |
| `2` | Unexpected system error (unhandled exception) |

All errors are printed to **`stderr`**. All result output goes to **`stdout`**.
This separation enables clean shell pipe usage: `circularocli.exe users search --format json | jq .`

---

## Architecture Decision Records (ADRs)

Detailed technical decisions and rationale are documented in two places:
- [`docs/decisions.md`](docs/decisions.md) — Summary ADR log
- [`docs/adr/0001-graalvm-native-build-configuration.md`](docs/adr/0001-graalvm-native-build-configuration.md)

The full **implementation plan audit trail** (17+ plans, one per feature/fix cycle) is in:
- [`docs/plans/`](docs/plans/)
