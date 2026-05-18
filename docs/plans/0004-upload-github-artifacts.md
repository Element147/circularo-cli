# 0004 - Upload GitHub Artifacts

## Triggering Prompt
> At the end of the github workflow, I would like to have a downloadable artefacts. One for jar, one for native windows and one for native linux.

## Plan & Steps
1. **Update CI Pipeline**: Edit `.github/workflows/ci.yml`.
2. **JAR Artifact**: Add a step to upload the Fat JAR (`build/libs/*-SNAPSHOT.jar`) using `actions/upload-artifact@v4`. This will only trigger on the `ubuntu-latest` matrix job to avoid duplicate JAR uploads.
3. **Native Executable Artifact**: Add a step to upload the native compiled executable from `build/native/nativeCompile/`. This step will run on both matrix OSes (Windows and Linux) and dynamically name the artifact based on the matrix OS (`circularo-cli-native-${{ matrix.os }}`).

## Architectural Decisions
- We use the official `actions/upload-artifact@v4` action.
- The JAR is OS-agnostic, so we only need to upload it once.
- The Native Image is OS-specific, so the matrix build will natively produce and upload both the Windows `.exe` and the Linux ELF binary.

## Expected Outcome
The GitHub Actions workflow will produce three downloadable zip artifacts:
- `circularo-cli-jar`
- `circularo-cli-native-ubuntu-latest` (Linux executable)
- `circularo-cli-native-windows-latest` (Windows executable)
