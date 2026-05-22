---
name: cli-command-conventions
description: >-
  Enforces naming, packaging, and structural conventions when creating
  or modifying CLI command classes. All new Picocli commands MUST follow
  this skill to maintain consistency across the codebase.
---

# CLI Command Conventions Skill

## Purpose

This skill ensures every CLI command class in the Circularo CLI project
follows the established conventions. Read and apply this skill **before**
creating, modifying, or reviewing any file under
`src/main/java/com/circularo/cli/cli/**`.

---

## 1. Package Placement

| Domain         | Package                                   | Group Command Class       |
|---------------|-------------------------------------------|---------------------------|
| Users          | `com.circularo.cli.cli.users`             | `UsersCmd.java`           |
| Documents      | `com.circularo.cli.cli.documents`         | `DocumentsCmd.java`       |
| Reports        | `com.circularo.cli.cli.reports`           | `ReportsCmd.java`         |
| Transactions   | `com.circularo.cli.cli.transactions`      | `TransactionsCmd.java`    |

> **Rule:** Every new API domain gets its own **plural** subpackage under `cli/`.

---

## 2. Group → Leaf Architecture

Every domain MUST have:

1. **One Group Command** — an empty `Runnable` that prints usage and lists `subcommands`:
   ```java
   @Component
   @Command(name = "<domain>", ..., subcommands = { <LeafCmd>.class, ... })
   public class <Domain>Cmd implements Runnable {
       @Spec private CommandSpec spec;
       @Override public void run() {
           spec.commandLine().usage(spec.commandLine().getOut());
       }
   }
   ```
2. **One or more Leaf Commands** — the actual logic.

> **Never** put business logic (API calls, formatting) in the group command.

---

## 3. Naming Convention

```
<Domain><Action>Cmd.java
```

Examples:
- `UsersSearchCmd.java`
- `DocumentsSearchCmd.java`
- `UsersDeletePhotoCmd.java`

The `@Command(name = "...")` value uses **kebab-case**:
- `search`, `get`, `delete`, `create`, `update`, `upload-photo`, `add-certificate`

---

## 4. Constructor — Only 2 Dependencies

Every leaf command constructor MUST inject exactly two dependencies:

```java
@Autowired
public <Domain><Action>Cmd(<ApiClient> apiClient, OutputHelper output) {
    this.apiClient = apiClient;
    this.output = output;
}
```

| Dependency   | Type                                       | Purpose                    |
|-------------|-------------------------------------------|----------------------------|
| `apiClient` | Domain-specific `*ApiClient` class        | REST API calls             |
| `output`    | `com.circularo.cli.cli.OutputHelper`       | Format dispatch            |

> **NEVER** inject `TableFormatter`, `JsonFormatter`, or `MarkdownFormatter` directly.
> The only exception is commands that need `ObjectMapper` (e.g., file-based payloads), which instantiate it locally.

---

## 5. FormatMixin — Mandatory

Every leaf command MUST include:

```java
@Mixin
private FormatMixin formatMixin;
```

> **NEVER** declare `@Option(names = {"-f", "--format"}, ...)` inline. Use `FormatMixin`.

---

## 6. Output Formatting — OutputHelper.format()

Replace all if/else format dispatch chains with:

```java
spec.commandLine().getOut().println(
    output.format(formatMixin.getFormat(),
        t -> t.formatXxx(data),
        j -> j.formatXxx(data),
        m -> m.formatXxx(data))
);
```

> **Key lambda rule:** If the data variable is reassigned (e.g., filtered), assign the final result to a new `final` variable before passing to lambdas.

---

## 7. Required Annotations

```java
@Component                                    // Spring Boot auto-discovery
@Command(name = "...",                        // kebab-case command name
         mixinStandardHelpOptions = true,      // --help, --version
         header = "...",                       // one-line summary
         description = "...")                  // detailed description
public class <Domain><Action>Cmd implements Runnable {

    @Spec
    private CommandSpec spec;                  // for accessing commandLine().getOut()

    @Mixin
    private FormatMixin formatMixin;           // shared --format option
```

---

## 8. Registering a New Command

After creating a leaf command:
1. Add it to the group command's `subcommands = { ... }` array
2. If it's a new domain, register the group in `RootCmd.java`'s `subcommands`
3. Update `docs/user_manual.md` with the new command syntax
4. Add tests following the established pattern (stub + 2-param constructor)

---

## 9. Test Conventions

- Tests live in the matching subpackage: `src/test/java/com/circularo/cli/cli/<domain>/`
- Each domain has a package-private `Stub*ApiClient` class
- Use `new <Cmd>(stubApiClient, outputHelper)` — 2-param constructor
- For testing the group command's usage output, create a `CommandLine.IFactory` that wires all leaf commands

---

## 10. Complete Command Template

```java
package com.circularo.cli.cli.<domain>;

import com.circularo.cli.api.<Domain>ApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.<ModelClass>;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(
        name = "<action>",
        mixinStandardHelpOptions = true,
        header = "<One-line summary>.",
        description = "<Detailed description of the command.>"
)
public class <Domain><Action>Cmd implements Runnable {

    private final <Domain>ApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    // Add @Parameters and @Option as needed

    @Autowired
    public <Domain><Action>Cmd(<Domain>ApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        var result = apiClient.someMethod(/* args */);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatXxx(result),
                        j -> j.formatXxx(result),
                        m -> m.formatXxx(result))
        );
    }
}
```
