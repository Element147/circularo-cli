# 0003 - Cross-Platform CI and Native Build Verification

## Triggering Prompt
> Also make sure that the gradle build wil work on both linux and windows. Also the github workflow will work on linux. And also the native build images will run on both windows and linux.

## Plan & Steps
1. **GitHub Actions Matrix Setup**: Modify `.github/workflows/ci.yml` to run a matrix strategy supporting both `ubuntu-latest` (Linux) and `windows-latest` (Windows).
2. **Setup MSVC Environment for Windows GraalVM Builds**: Include the `ilammy/msvc-dev-cmd@v1` action on the `windows-latest` runner to initialize the MSVC environment (`cl.exe`, etc.) required by GraalVM `native-image`.
3. **Cross-Platform Shell Configuration**: Use `shell: bash` explicitly on all workflow steps so Gradle wrapper commands (`./gradlew`) execute uniformly on both Linux and Windows.
4. **Validation**: Ensure standard test suites and native image compiles are fully verified on both local Windows dev environments and GitHub CI runners.

## Architectural Decisions
- **Compilation Platform Target**: GraalVM Native Image builds are inherently architecture and OS specific. To produce executables that run on Windows and Linux, they must be compiled on Windows and Linux runners respectively. The matrix strategy natively builds binary distributions for both target platforms.
- **Unified Step Scripts**: Using `shell: bash` simplifies the CI configuration by ensuring `./gradlew` runs seamlessly on both OSes (since Windows runners pre-install Git Bash).

## Expected Outcome
The GitHub Actions workflow will successfully trigger on both `ubuntu-latest` and `windows-latest`. The pipeline will compile standard Fat JARs and platform-native executables (`.exe` on Windows, standard ELF binary on Linux) and verify all automated tests run successfully on both platforms.
