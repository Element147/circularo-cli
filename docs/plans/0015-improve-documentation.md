# 0015 - Improve Documentation

**User Prompt:**
"I need to improve the documentation. Check what is currently used. I need one developer friendly documentation. Where if a new developer arrives, I want to present him an example set of commands how to set it up, how to build, how to run the app with full command line examples.

The readme needs to be simple enough where it will be easy also for non tech people to look up key parts of commands and documents.
Expect that somebody just opens the project want to read the readme, want to see how to build the application and quick test of few api calls using that.
And then if interested, there needs to be clearly stated that complete documentation can be seen in the user manual in the doc folder. So the readme, would work with paths like:
.\build\native\nativeCompile\circularocli.exe --version

but user manual would expect somethign like this - without the full path circularocli.exe --version.

Improve the documentation in that way."

## Proposed Technical Design/Steps

1. **Modify `README.md`**:
   - Keep the layout clean, simple, and high-quality.
   - Describe what the tool is and its benefits.
   - Add a step-by-step **Quick Start** guide:
     - Copy `.env.example` to `.env` and fill credentials.
     - Compile using `./gradlew nativeCompile`.
     - Test run using full local paths: `.\build\native\nativeCompile\circularocli.exe --version`, `.\build\native\nativeCompile\circularocli.exe users search`, `.\build\native\nativeCompile\circularocli.exe documents`.
   - Add a **Developer Setup Guide** section:
     - Prerequisites: GraalVM JVM (automatically handled by the Gradle toolchain; no manual installation needed).
     - Full build options: `./gradlew bootJar` vs. `./gradlew nativeCompile`.
     - Full test options: `./gradlew test` vs. `./gradlew nativeTest`.
     - Run options: `./gradlew bootRun --args="..."`, `java -jar build/libs/circularocli-0.0.1-SNAPSHOT.jar ...`.
   - Clearly state and link to the [User Manual](../user_manual.md) in the `docs` folder for advanced features, query patterns, and all 26+ subcommands.

2. **Modify `docs/user_manual.md`**:
   - Replace all occurrences of `.\build\native\nativeCompile\circularocli.exe` with `circularocli.exe`.
   - Ensure the command tables/examples also use `circularocli.exe` instead of `circularo-cli` to maintain consistency.

## Post-Execution Task Checklist

- [x] Update `README.md` to match the requirements (simplified structure, full-path quick start, complete developer guide, clear links)
- [x] Update `docs/user_manual.md` (replace `.\build\native\nativeCompile\circularocli.exe` with `circularocli.exe` everywhere)
- [x] Verify build compiles correctly using Gradle wrapper
- [x] Test the application commands to make sure everything works
