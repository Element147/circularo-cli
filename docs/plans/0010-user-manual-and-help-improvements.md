# 0010 - User Manual and Help Improvements

## Exact User Prompt

> I miss this kind of info here. Create a user manual in the doc folder. Mention in readme, that there is a user manual flow how to use it. Like full documentation about all posible combinations which are possible to use here. Also make rule, that every time when a new possible command is introduced, everything is documented in the new user manual. The user manual needs to be easy enough to be understod by beginners and non tech people. I want to also extend the help to be more user friendly. Create an implementation plan for this. I dont want to rely only on help commands but also on user manual in documentation. So there would be two synchronized sources of truth.

---

## Proposed Changes

### 1. Repository Rules Update
We will introduce a strict dual-synchronization rule to ensure both source-code command definitions (Picocli help) and the user manual are updated in tandem.

#### [MODIFY] [agents.md](file:///c:/Git/circularo-cli/agents.md)
- Update trigger rules or add a rule in `3. Implementation Plan Tracking (MANDATORY)` or section `5` to enforce documenting all new command parameters in the user manual.

### 2. User Manual Creation
We will create a comprehensive, beginner-friendly Markdown manual in the documentation folder.

#### [NEW] [user_manual.md](file:///c:/Git/circularo-cli/docs/user_manual.md)
- Write a non-programmer guide covering:
  - **Prerequisites**: Setting up `.env` (API URL & Token).
  - **Standard Run Commands**: Explain how to run the tool (Fat JAR and Native Binary).
  - **Searching Users (`users` command)**: Examples of full searches, field-specific searches (e.g. `mail:xxx`, `fullname:xxx`), and wildcards.
  - **Searching Documents (`documents` command)**: Examples of document searches (e.g. `title:xxx`, status filters).
  - **Formatting Results**: Explain the difference between `--format table` (readable grid) and `--format json` (raw text structure).
  - **Troubleshooting FAQ**: Friendly description of common errors (401 invalid credentials, 400 malformed query, 404 URL route issue).

### 3. README Reference
Update `README.md` to point users to the manual.

#### [MODIFY] [README.md](file:///c:/Git/circularo-cli/README.md)
- Add a dedicated "User Manual" section near the top linking to `docs/user_manual.md`.

### 4. Interactive Console Help Enhancements
Update Picocli `@Command` and `@Option` configurations in Java source files to make output help friendly and detailed.

#### [MODIFY] [RootCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/RootCmd.java)
- Add detailed description header and sub-command usage quick examples.

#### [MODIFY] [UsersCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/UsersCmd.java)
- Expand command description, option descriptions, and add concrete examples of querying users in the console help output.

#### [MODIFY] [DocumentsCmd.java](file:///c:/Git/circularo-cli/src/main/java/com/circularo/cli/cli/DocumentsCmd.java)
- Expand command description, option descriptions, and add concrete examples of querying documents in the console help output.

---

## Verification Plan

### Automated Tests
- Run Gradle test suite to verify no syntax errors or Picocli instantiation issues:
  ```powershell
  ./gradlew test
  ./gradlew nativeTest
  ```

### Manual Verification
- Recompile the native binary:
  ```powershell
  ./gradlew nativeCompile
  ```
- Run help commands to inspect layout:
  ```powershell
  .\build\native\nativeCompile\circularocli.exe --help
  .\build\native\nativeCompile\circularocli.exe users --help
  .\build\native\nativeCompile\circularocli.exe documents --help
  ```
- Inspect generated `user_manual.md` content and formatting.
