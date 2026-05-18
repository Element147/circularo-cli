# Skill: Deterministic Output

**Trigger:** `UI & Command Layer`
**Context:** When the user asks to add a new command, parse arguments, or you are editing files in `src/main/java/com/circularo/cli/cli/*`

## Rules
- Always ensure the command accepts a `--format` flag (e.g., JSON, TEXT, CSV).
- Route the API response to the `format` package for processing.
- Never use raw `System.out.println` inside command classes; rely on standard formatting services to output results.
