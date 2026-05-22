# Plan 0019: Documentation Review and Command Verification

## User Prompt

> Now do review of the documenations and test whether still all commands stated in there still works. /goal  Create a plan for checking all of them and then test it one by one, until you are sure, it works as expected.

---

## Technical Design & Strategy

We will review the CLI user manual (`docs/user_manual.md`), list every command mentioned, and systematically execute it in two modes:
1. **JVM Mode** (`./gradlew bootRun --args="..."`)
2. **Native Compiled Binary Mode** (`.\build\native\nativeCompile\circularocli.exe ...`)

Since this task is in `/goal` mode, we must be extremely thorough, test every single option variation, verify outputs against the expected formatting, and check error handling behavior.

### Critical Observations:
- **Group Commands vs API Calls:** Under the new package reorganization, group commands like `users`, `documents`, `reports`, `transactions` simply display Picocli help text and do not invoke any API client. Therefore, Section 9 (Troubleshooting) in `docs/user_manual.md` which lists `.\circularocli.exe users` as triggering API errors (e.g. missing credentials, connection refused) is no longer technically accurate. It should trigger usage info instead. To trigger actual API-level error scenarios, subcommands that communicate with the server (like `users search`) must be used. We will correct these in the User Manual.
- **Resource IDs:** Commands like `users get <id>`, `users delete <id>`, etc. require concrete IDs. We will first run search queries to fetch real, valid resource IDs from the sandbox API, then use those IDs to run the specific commands.
- **Test Data Files:** Commands like `users create --file ./new_users.json` need actual, valid payload files. We will create mock input files in a temporary directory or in the project root to test these commands.
- **Cleanup:** Any mutating operations (creating a user, uploading a photo, adding a group/certificate) will be followed by their corresponding cleanup commands (deleting the user, deleting the photo, removing the group/certificate) to keep the environment clean.

---

## Proposed Verification Checklist

### Phase 1 — Command Group Help & Version Verification
- [x] Test global help and version:
  - `.\circularocli.exe --help`
  - `.\circularocli.exe --version`
- [x] Test base group command usage outputs (which should just print Picocli help, not perform API calls):
  - `.\circularocli.exe users`
  - `.\circularocli.exe documents`
  - `.\circularocli.exe reports`
  - `.\circularocli.exe transactions`

### Phase 2 — Users Subcommands Verification
- [x] **Search & Retrieval:**
  - `users search` (Table, JSON, MD formats)
  - `users search -q "Lubo"`
  - `users search --limit 10`
  - `users search -q "mail:*@seznam.cz" -f json`
  - `users get <id>` (retrieve real ID from search)
  - `users get-by-username <username>`
  - `users rights <id>` (with and without `--detailed`)
- [x] **Account CRUD Operations:**
  - `users create --file ./new_users.json` (verified using `temp_user_unique@example.com` payload)
  - `users update --file ./update_users.json` (verified bulk update)
  - `users update-single <id> --file ./user_properties.json` (verified updating single user properties)
  - `users delete <id>` (verified user deletion)
  - `users register` with all required options (verified graceful 401 failure as self-registration is disabled)
- [x] **Authentication & Security:**
  - `users activate` with all required options (verified graceful 400 failure since user is already activated)
  - `users resend-activation <id> --tenant <tenant-name>` (verified graceful 400 failure since user is active)
  - `users check-activation` (verified success output)
  - `users change-password <id> --new "NewSecr3t" --current "OldSecr3t"` (verified password change)
  - `users check-password --password "weakpass" --tenant <tenant-name>` (verified strength check scoring)
  - `users reset-password --username joesmith --code 987654 --password "NewPassword123"` (verified graceful 400 failure)
  - `users reset-password-username joesmith --tenant <tenant-name>` (verified password reset flow initiation)
  - `users check-reset-token --token <reset-token>` (verified graceful 400 failure)
- [x] **Group Management:**
  - `users add-group <id> --group <groupname>` (verified graceful 404 failure for nonexistent group)
  - `users remove-group <id> --group <groupname>` (verified success)
- [x] **Profile Assets & Certificates:**
  - `users upload-photo <id> --file ./photo.jpg` (verified photo upload using generated avatar PNG)
  - `users delete-photo <id>` (verified photo deletion)
  - `users delete-signature <id>` (verified signature deletion)
  - `users delete-initials <id>` (verified initials deletion)
  - `users add-certificate <id> --name "SigningCert" --type "personal" --file ./cert.p12` (verified Picocli option synchronization)
  - `users remove-certificate <id> --cert-id=<certificateId>` (verified Picocli option synchronization and deletion)

### Phase 3 — Documents Subcommands Verification
- [x] `documents search`
- [x] `documents search -f json`
- [x] `documents search -q 'title:"Circularo User Guide.pdf"'`
- [x] `documents search -q "definitionType:ext"`
- [x] `documents search -q "title:Invoice*"`
- [x] `documents search -q "status:signed"`
- [x] `documents search -q "status:signed AND title:*contract*"`
- [x] `documents search --limit 10`

### Phase 4 — Reports Subcommands Verification
- [x] `reports search` (Verified graceful 403 Forbidden failure due to sandbox export rights limits)
- [x] `reports search --verbose`
- [x] `reports search -q "title:\"NDA Agreement.pdf\""`
- [x] `reports search -q "owner:Lubos*"`
- [x] `reports search -q "sender:Lubos*"`
- [x] `reports search --limit 10`

### Phase 5 — Transactions Subcommands Verification
- [x] `transactions search` (Verified graceful 403 Forbidden failure due to sandbox export rights limits)
- [x] `transactions search --state completed --verbose`
- [x] `transactions search --state completed`
- [x] `transactions search --state inprogress`
- [x] `transactions search -q "title:Invoice*" --state draft`
- [x] `transactions search --limit 25`

### Phase 6 — Error & Troubleshooting Scenarios Verification
- [x] **Scenario A: Missing Credentials** (Renamed `.env` and verified `users search` fails)
- [x] **Scenario B: Authorization Failure** (Verified `reports search` fails with 403 Forbidden)
- [x] **Scenario C: Malformed Input** (Verified `users search -q "Luboš"` fails with 400 Bad Request)
- [x] **Scenario D: Connection Refused** (Overrode base URL with invalid address and verified network error output)

### Phase 7 — User Manual & Help Synchronization
- [x] Verify help text for every command is correct and synchronized.
- [x] Update `docs/user_manual.md` to correct any outdated commands (e.g. error scenario triggers needing subcommands instead of base group commands, or formatting details).
