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
- Every search or fetch command must support a `--format` flag (e.g., `json`, `md`, `tabular`).
- Do not use raw `System.out.println` within the controller classes. Route responses to an output formatting utility that renders the output deterministically based on the chosen format.
- Output field ordering and names should remain stable across invocations.

## 3. Help Messages
- When adding or modifying options or subcommands, ensure `@Command(description = "...")` and `@Option(description = "...")` are fully descriptive.
- Always keep the Picocli help annotations synchronized with the User Manual (`docs/user_manual.md`).
