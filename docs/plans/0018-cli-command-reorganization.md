# Plan 0018: CLI Command Layer Reorganization

## User Prompt

> I can see that the cli folder has many java classes with almost the same functionality. Is it possible to unify them? Like for user related tasks have a userSearchCmd with the elasticsearch related functionality and userAPICMD where the rest of user operations will be present? Does it make sense to have it that way? Or is it better to keep it as it is, but just organize it into folders. Like have one folder for user related cmd, one for document related cmd etc? What is the best practice and makes more sense in this case? Also expect that the number of the cmd operations will grow in the future which might cover all of the rest APIs according to the swagger documentation.

**User decisions:**
- Proceed with Option C (subpackages + boilerplate extraction)
- Use **plural** subpackage names
- Split Documents/Reports/Transactions into group-command + leaf-command pattern
- Create a **new agent skill** to enforce conventions

---

## Technical Design

### Phase 1 — Shared Boilerplate Components
- `FormatMixin.java`: Picocli `@Mixin` centralizing the `--format` option
- `OutputHelper.java`: Spring `@Component` wrapping all 3 formatters with a generic `format()` dispatch using `java.util.function.Function`

### Phase 2 — Users Subpackage Migration
- Moved all 25 user command files from `cli/` → `cli/users/`
- Refactored each: 4-param constructor → 2-param (`ApiClient` + `OutputHelper`), if/else chain → `output.format(...)`

### Phase 3 — Documents/Reports/Transactions Split
- Each monolithic command split into group + leaf pattern
- **Breaking change**: `documents` / `reports` / `transactions` now require `search` subcommand

### Phase 4 — Tests, RootCmd, Docs
- All test files moved to matching subpackages
- `RootCmd.java` imports updated
- `CircularoExecutionExceptionHandlerTest` updated with local stub
- New `OutputHelperTest` created
- `ARCHITECTURE.md` and `docs/user_manual.md` updated

### Phase 5 — Agent Skill + AGENTS.md
- Created `.agents/skills/cli-command-conventions/SKILL.md`
- Updated `AGENTS.md` with new trigger
- Updated `.agents/skills/cli-boundaries-and-formatting/SKILL.md`

---

## Post-Execution Checklist

- [x] Create `FormatMixin.java`
- [x] Create `OutputHelper.java`
- [x] Move all 25 user commands to `cli/users/` with refactored pattern
- [x] Split `DocumentsCmd` → group + `DocumentsSearchCmd` in `cli/documents/`
- [x] Split `ReportsCmd` → group + `ReportsSearchCmd` in `cli/reports/`
- [x] Split `TransactionsCmd` → group + `TransactionsSearchCmd` in `cli/transactions/`
- [x] Delete old command files from `cli/`
- [x] Update `RootCmd.java` imports
- [x] Move and refactor all test files
- [x] Create `OutputHelperTest.java`
- [x] Update `CircularoExecutionExceptionHandlerTest`
- [x] Update `ARCHITECTURE.md`
- [x] Update `docs/user_manual.md`
- [x] Create `.agents/skills/cli-command-conventions/SKILL.md`
- [x] Update `AGENTS.md`
- [x] Update `.agents/skills/cli-boundaries-and-formatting/SKILL.md`
- [x] `./gradlew test` passes — 73 tests
- [x] `./gradlew nativeTest` passes — 73 tests
- [x] `./gradlew bootRun --args="users"` works — 25 subcommands
- [x] `./gradlew nativeCompile` builds — 0.061s startup
