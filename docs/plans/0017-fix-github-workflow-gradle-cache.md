# 0017 - Fix GitHub Workflow Gradle Cache

**User Prompt:**
"I'll retrieve the logs for this failing job to identify the issue and provide a solution.Now let me get the workflow file to understand the job definition:## Solution

The job is failing because the Gradle cache is not being found on the Windows runner. This happens when the graalvm/setup-graalvm action's cache configuration encounters an issue on Windows systems.

Root Cause
The workflow specifies cache: 'gradle' in the GraalVM setup action, but the cache restoration is failing on the Windows runner. This can occur due to:

Cache key mismatches between runs
File path issues on Windows
Gradle daemon interference
Recommended Solution
Update your .github/workflows/ci.yml to disable Gradle caching in the GraalVM action and manage it separately with the official gradle/gradle-build-action:

YAML
jobs:
  build:
    strategy:
      fail-fast: false
      matrix:
        os: [ubuntu-latest, windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/checkout@v5

      - name: Setup GraalVM 25
        uses: graalvm/setup-graalvm@v1
        with:
          java-version: '25'
          distribution: 'graalvm-community'
          github-token: ${{ secrets.GITHUB_TOKEN }}
          # Remove cache: 'gradle' line

      - name: Setup Gradle
        uses: gradle/gradle-build-action@v3

      - name: Configure Developer Command Prompt for MSVC
        if: matrix.os == 'windows-latest'
        uses: ilammy/msvc-dev-cmd@v1

      - name: Build and Test (Fat JAR)
        shell: bash
        run: ./gradlew build

      - name: Build Native Image
        shell: bash
        run: ./gradlew nativeCompile

      # ... rest of workflow
Key changes:

Remove cache: 'gradle' from the GraalVM setup action
Add the official gradle/gradle-build-action@v3 which has better Windows compatibility and cache management
This approach separates concerns and ensures proper cache handling across both Ubuntu and Windows runners.

I have this given error. Fix it."

## Proposed Technical Design/Steps

1. **Remove `cache: 'gradle'` from GraalVM Setup**:
   - In [.github/workflows/ci.yml](../../.github/workflows/ci.yml), edit the `Setup GraalVM 25` step to remove the `cache` property.

2. **Add setup-gradle Action**:
   - Add `gradle/actions/setup-gradle@v4` action step right after the GraalVM setup to manage Gradle setup and cache handling properly.

3. **Verify Local Build/Tests**:
   - Run local unit/integration tests to ensure no local regression (`./gradlew test`).

## Post-Execution Task Checklist

- [x] Update [.github/workflows/ci.yml](../../.github/workflows/ci.yml) step `Setup GraalVM 25` to remove `cache: 'gradle'`
- [x] Add `Setup Gradle` step using `gradle/actions/setup-gradle@v4` in [.github/workflows/ci.yml](../../.github/workflows/ci.yml)
- [x] Run `./gradlew test` to verify local execution
