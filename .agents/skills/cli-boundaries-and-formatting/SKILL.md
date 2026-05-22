---
name: cli-boundaries-and-formatting
description: Guidance on Picocli boundaries, CLI command structure, and output formatting.
---

# Picocli Commands & Boundaries

## 1. Strict Layer Separation
- **Picocli classes** (annotated with `@Command` or `@Option`) must act strictly as a parser and controller layer.
- **DO NOT** put business logic, API calls, or formatting code directly inside Picocli commands.
- **DO** inject services (e.g., query managers, API clients) via the constructor.
- **DO** route API responses to the formatting package or service for output generation.

## 2. Deterministic Output with `--format`
- Every search or fetch command must support a `--format` flag via `@Mixin FormatMixin` (never inline `@Option`).
- Formatting dispatch must go through `OutputHelper.format(...)` — never inject `TableFormatter`, `JsonFormatter`, or `MarkdownFormatter` directly.
- Do not use raw `System.out.println` within the controller classes. Use `spec.commandLine().getOut().println(...)`.
- Output field ordering and names should remain stable across invocations.
- See `.agents/skills/cli-command-conventions/SKILL.md` for the full command template.

## 3. Help Messages
- When adding or modifying options or subcommands, ensure `@Command(description = "...")` and `@Option(description = "...")` are fully descriptive.
- Always keep the Picocli help annotations synchronized with the User Manual (`docs/user_manual.md`).
