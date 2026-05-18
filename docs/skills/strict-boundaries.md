# Skill: Strict Boundaries

**Trigger:** `UI & Command Layer`
**Context:** When the user asks to add a new command, parse arguments, or you are editing files in `src/main/java/com/circularo/cli/cli/*`

## Rules
- Put only Picocli `@Command` and `@Option` logic in these classes.
- Do NOT write HTTP calls or output formatting directly in the CLI command classes.
- Inject necessary services and API clients via the constructor.
