# Skill: Resilience

**Trigger:** `API & Network Layer`
**Context:** When the user asks to fetch data, handle JSON, or you are editing files in `src/main/java/com/circularo/cli/api/*`

## Rules
- Always wrap HTTP calls in try-catch blocks handling `IOException` and `InterruptedException`.
- Map HTTP 4xx and 5xx response codes to the custom `CircularoException`.
