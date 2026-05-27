# Plan 0020: Simplify Demo Showcase for Beginners

## User Prompt

> Now I need to update the demo_showcase.md 
> 
> I need you to asume, somebody who has zero development knowledge, does not know how java works and is complete beginer, will be able with this guide start this command line. It needs to be simple, straightforward, easy to use. Or if you feel like the user_manual is a better place, modify that one. I have a github workflow, that builds linux and windows native images. So I want to have a step by step guide that is really simple that will guide to download the app in https://github.com/Element147/circularo-cli/actions/runs/26445127835, and then depending whether the user is using linux or windows, there shall be always a windows full example and linux alternative. It shall also contain the full walkthourg like in the use_manual.md file. 
> And the end I want to just send this file to any person and he will be able to follow this guide where it will be explained what to dowload, how to setup environment variables and how to execute it.

*Additional requirement added by user:*
> Also explain in that guide, that no Java is required, if only final build is used, because everything is build in github.

---

## Technical Design & Strategy

The current `demo_showcase.md` is targeted towards developers, featuring instructions on building the JVM version and running GraalVM native compiles locally. We will completely rewrite `docs/demo_showcase.md` to be an ultimate, end-to-end beginner guide for non-technical users who want to run the Circularo CLI using prebuilt native binaries without knowing anything about Java, Gradle, or build processes.

### Structure of the New `demo_showcase.md`:

1. **Introduction**: What is this tool? Keep it simple, friendly, and visual.
   - **CRITICAL NOTE**: Explicitly explain that **NO JAVA installation is required** on the user's machine because the app is already prebuilt as a native application on GitHub.
2. **Step 1: Download the App**:
   - Clear link to the specific GitHub Action run: [https://github.com/Element147/circularo-cli/actions/runs/26445127835](https://github.com/Element147/circularo-cli/actions/runs/26445127835).
   - Instructions on which file to download depending on the operating system:
     - **Windows users**: Download the Windows native binary artifact (usually a `.zip` containing `circularocli.exe`).
     - **Linux users**: Download the Linux native binary artifact (usually a `.zip` containing a `circularocli` execution binary).
   - Instructions on where to extract the downloaded file (e.g., a simple folder like `C:\circularo-cli` or `~/circularo-cli`).
3. **Step 2: Configuration (.env file / environment variables)**:
   - Super simple guide to setting up environment variables.
   - Explain how to create the `.env` file from `.env.example` using plain language (Notepad or Text Editor).
   - Alternatively, explain how to set it via command line temporarily if preferred (with Windows/Linux comparison).
   - Clearly state the two properties to configure:
     - `CIRCULARO_API_URL`
     - `CIRCULARO_API_TOKEN` (including brief info on how to generate it on their Circularo web interface).
4. **Step 3: Opening the Command Terminal**:
   - How to open PowerShell/CMD on Windows.
   - How to open Terminal on Linux.
   - How to "navigate" (change directory) to the extracted folder in a very friendly, beginner-friendly way.
5. **Step 4: Executing Commands (Windows vs Linux side-by-side examples)**:
   - Provide parallel examples for **every** command: the Windows version (PowerShell `.\circularocli.exe`) and the Linux version (`./circularocli`).
   - Group them clearly:
     - **Verification**: `--help` and `--version`
     - **Users**: Search, detailed view, bulk actions, password changing, group management, profile assets.
     - **Documents**: Searching with specific filters (exact, wildcards, types, JSON formatting).
     - **Workflow Reports**: High-level search and verbose search.
     - **Transactions**: State-based filtering and verbose participant paths.
6. **Step 5: Formatting Outputs (Table vs JSON vs Markdown)**:
   - Explain the `--format` option simply.
7. **Step 6: Understanding Errors (Troubleshooting)**:
   - Simple guide to reading stderr messages if things go wrong (e.g. missing .env, 403 Forbidden, 400 Malformed query).

---

## Proposed Verification Checklist

### Phase 1 — Create and Populate Plan
- [x] Create plan `docs/plans/0020-simplify-demo-showcase-for-beginners.md` with triggering prompt and steps.

### Phase 2 — Draft the New `demo_showcase.md`
- [ ] Draft a beginner-friendly structure of the entire manual.
- [ ] Write step-by-step download directions using the specific link.
- [ ] Provide explicit Notepad/TextEditor setup details for `.env`.
- [ ] Provide step-by-step terminal navigation guide (`cd` explained simply).
- [ ] Include detailed command examples (each having a Windows PowerShell line and a Linux shell line).
- [ ] Incorporate the full walkthrough contents like `user_manual.md`.

### Phase 3 — Verify and Finalize
- [ ] Review the updated file to make sure it contains no jargon, is easy to read, and is complete.
