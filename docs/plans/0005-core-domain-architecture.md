# 0005 – Core Domain Architecture

## Exact User Prompt

> Act as an Expert Java Architect. We are continuing the development of the Circularo CLI utility. The base Spring Boot 3 + Picocli + Gradle + Java 25 project is already set up and running.
>
> Please review the workspace and execute the following tasks step-by-step to build the core domain architecture:
>
> ### Task 1: Create the Package Structure
> Under `src/main/java/com/circularo/cli/`, create the following sub-packages to enforce strict separation of concerns:
> - `cli` (for Picocli command implementations)
> - `api` (for the native HTTP client and exceptions)
> - `config` (for configuration, environment, and credential handling)
> - `format` (for output presentation logic)
> - `model` (for Java Records representing domain entities and DTOs)
>
> ### Task 2: Implement ConfigLoader (Fallback Pattern)
> Create `com.circularo.cli.config.ConfigLoader` annotated with `@Component`. We need a secure, multi-layered approach for resolving the API token to support both CI/CD automation and local developer experience.
> Implement a method `public String getApiToken()` with this exact fallback logic:
> 1. **Primary:** Check the Environment Variable `CIRCULARO_API_TOKEN`. If present and not blank, return it.
> 2. **Fallback:** Check a local properties file at `~/.circularo/config.properties` (user's home directory). If the file exists and contains the key `api.token`, return its value.
> 3. **Failure:** If neither is found, throw a clear `IllegalStateException` (or a custom `CircularoException` if you generate one in the `api` package) with the message: "API token not found. Please set CIRCULARO_API_TOKEN env variable or configure ~/.circularo/config.properties".
> *Note: Do not add CLI arguments for tokens to prevent leakage in shell history.*
>
> ### Task 3: CLI Command Skeletons
> 1. Create two new classes in the `cli` package: `UsersCmd.java` and `DocumentsCmd.java`.
> 2. Annotate both with `@Component` and the appropriate `@Command` annotation (e.g., `name = "users", description = "Search users"`).
> 3. Both must implement `Runnable`. In their `run()` methods, simply print a placeholder using standard output: "Executing search... (API integration pending)".
> 4. Update the existing `RootCmd.java` base command to register these new commands using the subcommands attribute: `@Command(..., subcommands = {UsersCmd.class, DocumentsCmd.class})`.
>
> Execute these tasks and briefly confirm when done.

---

## Objective

Establish the core domain package structure and skeleton implementations for the Circularo CLI, enforcing strict separation of concerns and laying the foundation for subsequent API integration work.

## Steps

### Task 1 – Package Structure
Create placeholder `.gitkeep` files (or initial classes) to anchor the five new packages:
- `com.circularo.cli.cli`
- `com.circularo.cli.api`
- `com.circularo.cli.config`
- `com.circularo.cli.format`
- `com.circularo.cli.model`

### Task 2 – `CircularoException` + `ConfigLoader`
1. Create `com.circularo.cli.api.CircularoException` – a runtime exception with a clean constructor.
2. Create `com.circularo.cli.config.ConfigLoader` (`@Component`) with `getApiToken()`:
   - Reads `System.getenv("CIRCULARO_API_TOKEN")` first.
   - Falls back to `~/.circularo/config.properties` → key `api.token`.
   - Throws `CircularoException` if neither found.

### Task 3 – Command Skeletons
1. Create `com.circularo.cli.cli.UsersCmd` (`@Component`, `@Command(name="users", ...)`, `Runnable`).
2. Create `com.circularo.cli.cli.DocumentsCmd` (`@Component`, `@Command(name="documents", ...)`, `Runnable`).
3. Refactor `RootCmd.java` to:
   - Move to the root package (no move needed – it stays at `com.circularo.cli`).
   - Add `subcommands = {UsersCmd.class, DocumentsCmd.class}` to `@Command`.
   - Switch from `Callable<Integer>` to `Runnable` (consistent with sub-commands; or keep `Callable` — keeping `Callable` is fine, just adding subcommands).

## Verification
- `./gradlew test`
- `./gradlew nativeTest`

## Expected Outcome
A compilable project with the full package scaffold, a secure token-resolution strategy, and two wired sub-commands ready for API integration.
