# Skill: Graceful Failure

**Trigger:** `Exception Handling`
**Context:** When the user asks to fix an error, or you are writing `catch` blocks or error classes.

## Rules
- Do NOT dump raw stack traces to the output.
- Distinguish between common errors such as missing credentials, HTTP 401/403, and network errors.
- Print a clean, actionable message to `System.err` guiding the user on how to fix the issue.
