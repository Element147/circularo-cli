---
name: error-handling-and-logging
description: Guidance on exception handling, exit codes, and graceful CLI failure.
---

# Graceful Error Handling and Logging

## 1. No Raw Stack Traces
- **NEVER** print raw stack traces to the user (e.g. `e.printStackTrace()`) unless a verbose/debug flag is explicitly enabled.
- Handle exceptions at the command entry point and format them cleanly.

## 2. Distinguish Error Scenarios
- Provide distinct, actionable error messages for:
  - Missing/Invalid credentials (e.g., missing API tokens, `.env` file configuration issues).
  - Authorization / Forbidden errors (HTTP 401/403).
  - Malformed query or input parameters.
  - Upstream network or server errors.
- Always output clean error messages directly to `System.err`.

## 3. Exit Codes
- Return appropriate exit codes representing the failure type (e.g., standard Picocli exit codes, or custom exit codes for authentication/connection failures) to allow downstream automation tools to react correctly.
