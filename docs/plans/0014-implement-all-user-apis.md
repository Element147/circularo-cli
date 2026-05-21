# 0014 - Implement All User APIs

**User Prompt:**
"I have already made comment to implementation plan. Implement it. Create a checkbox for each operation before the start of the implementation and continue one by one implementing them untill fully done. Write a unit test for each of them."

## Proposed Technical Design/Steps

1. **Model Expansion**:
   - Done. Defined all 26 endpoint request/response models in `com.circularo.cli.model.UserModels`.
   - Done. Added reflection hints for GraalVM native image compilation in `UsersApiClient`.

2. **API Client Implementation (`UsersApiClient.java`)**:
   - Done. Implemented all 26 endpoints using `java.net.http.HttpClient` via `BaseCircularoApiClient`.
   - Done. Added a custom multipart helper `buildMultipartBody()` to handle image/file uploads (photo, signature, initials, certificates).

3. **CLI Refactoring (`UsersCmd.java` and new Subcommands)**:
   - Convert `UsersCmd` into a command group that registers dedicated subcommands:
     - `UsersSearchCmd` (existing search logic, moved to its own subcommand to separate Elasticsearch from pure APIs)
     - `UsersGetCmd`
     - `UsersDeleteCmd`
     - `UsersCreateCmd`
     - `UsersUpdateCmd`
     - `UsersUpdateSingleCmd`
     - `UsersGetByUsernameCmd`
     - `UsersRightsCmd`
     - `UsersActivateCmd`
     - `UsersResendActivationCmd`
     - `UsersCheckActivationCmd`
     - `UsersChangePasswordCmd`
     - `UsersCheckPasswordCmd`
     - `UsersResetPasswordCmd`
     - `UsersResetPasswordUsernameCmd`
     - `UsersCheckResetTokenCmd`
     - `UsersAddGroupCmd`
     - `UsersRemoveGroupCmd`
     - `UsersUploadPhotoCmd`
     - `UsersDeletePhotoCmd`
     - `UsersDeleteSignatureCmd`
     - `UsersDeleteInitialsCmd`
     - `UsersAddCertificateCmd`
     - `UsersRemoveCertificateCmd`
     - `UsersRegisterCmd`
   - Inject `UsersApiClient` and the formatter classes into each subcommand.
   - Accept the `--file` option for commands that upload binary/local file data, reading files locally and transmitting them via multipart or base64.

4. **Formatting Extension**:
   - Extend `OutputFormatter`, `TableFormatter`, `JsonFormatter`, and `MarkdownFormatter` to support formatting the new types:
     - `GenericStatus`
     - `BulkResponse`
     - `UserRightItem` list
     - `UserActivation`
     - `UserResetToken`
     - `LoginResponse`
     - `UserConfiguration`
     - `PasswordStrength`

5. **Testing**:
   - Write comprehensive unit tests for each subcommand in `UsersCmdTest.java` (or separate test files per subcommand).
   - Use stubs or mocks to verify HTTP request path, input parsing, options validation, and output formatting.

6. **Documentation**:
   - Update `docs/user_manual.md` to document every command option, behavior, and output format.

## Post-Execution Task Checklist

- [x] Convert `UsersCmd` into a command group with subcommands
  - [x] `search` (Elasticsearch query)
  - [x] `get` (Retrieve a single user by ID)
  - [x] `delete` (Delete a user account)
  - [x] `create` (Create new user accounts)
  - [x] `update` (Update users bulk)
  - [x] `update-single` (Update single user)
  - [x] `get-by-username` (Get simple user info by username)
  - [x] `rights` (Get user rights)
  - [x] `activate` (Activate user)
  - [x] `resend-activation` (Resend activation email)
  - [x] `check-activation` (Check activation token validity)
  - [x] `change-password` (Change any user password)
  - [x] `check-password` (Check password strength)
  - [x] `reset-password` (Reset password by username)
  - [x] `reset-password-username` (Reset password by username. Optional captcha.)
  - [x] `check-reset-token` (Checks reset token validity)
  - [x] `add-group` (Add user to group)
  - [x] `remove-group` (Remove user from group)
  - [x] `upload-photo` (Upload photo)
  - [x] `delete-photo` (Delete profile image)
  - [x] `delete-signature` (Delete signature image)
  - [x] `delete-initials` (Delete initials image)
  - [x] `add-certificate` (Add certificate to the user)
  - [x] `remove-certificate` (Remove certificate from the user)
  - [x] `register` (Register a new user account)
- [x] Write Unit Tests for each method/command
- [x] Update `docs/user_manual.md` and Picocli annotations
- [x] Verify build (`./gradlew test` and `./gradlew nativeCompile`)
