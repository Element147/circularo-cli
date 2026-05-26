# Circularo CLI - Demo & Showcase Script

This document provides a simple, copy-paste-ready script for demonstrating the Circularo CLI. It covers the JVM build lifecycle, the GraalVM native build lifecycle, and an extensive suite of command variations.

---

## 1. JVM (Java) Lifecycle: Build, Test & Run
*Start your demo by showing the standard Java experience.*

```powershell
# 1. Run all Unit & Integration Tests on the JVM
./gradlew test

# 2. Build the standard Spring Boot executable JAR
./gradlew build

# 3. Run the CLI using the Spring Boot runner (JVM mode)
./gradlew bootRun --args="--version"

# 4. Alternatively, execute a search query via bootRun
./gradlew bootRun --args="users search"

./gradlew bootRun -q --args="--help"

---

## 2. GraalVM Lifecycle: Native Build & Run
*Showcase the power of Ahead-Of-Time (AOT) compilation for lightning-fast startup times.*

```powershell
# 1. Run native tests to ensure compatibility with GraalVM AOT
./gradlew nativeTest

# 2. Compile the application into a standalone Windows executable (.exe)
./gradlew nativeCompile

# 3. Verify the native binary was created and check version (Instant startup!)
.\build\native\nativeCompile\circularocli.exe --version
```

---

## 3. Comprehensive Command Variations (Native Execution)
*Use the generated native executable (`circularocli.exe`) for all the following demo commands.*

### System & Global Flags
```powershell
# Show global help and available command groups
.\build\native\nativeCompile\circularocli.exe --help

# Show version information
.\build\native\nativeCompile\circularocli.exe --version
```

### Users Domain
```powershell
# List all subcommands available for user management
.\build\native\nativeCompile\circularocli.exe users

# 1. Basic user search (Default table format)
.\build\native\nativeCompile\circularocli.exe users search

# 2. Search users by name query
.\build\native\nativeCompile\circularocli.exe users search -q "Lubo"

# 3. Search users by email domain and format output as JSON
.\build\native\nativeCompile\circularocli.exe users search -q "mail:*@seznam.cz" -f json

# 4. Limit search results to exactly 3 records
.\build\native\nativeCompile\circularocli.exe users search --limit 3

# 5. Output search results as Markdown (GFM)
.\build\native\nativeCompile\circularocli.exe users search -f md

# 6. Retrieve a specific user by their username (name field)
.\build\native\nativeCompile\circularocli.exe users get-by-username "joesmith"
```

*(Note: For the following operations, replace `<id>` with an actual ID from the search results, and ensure the `.json` / `.jpg` payload files are present in your working directory.)*

```powershell
# 7. Get detailed rights/permissions for a user
.\build\native\nativeCompile\circularocli.exe users rights <id> --detailed

# 8. Create a new user (Requires a valid new_users.json file)
.\build\native\nativeCompile\circularocli.exe users create --file ./new_users.json

# 9. Update multiple users (Requires a valid update_users.json file)
.\build\native\nativeCompile\circularocli.exe users update --file ./update_users.json

# 10. Change user password
.\build\native\nativeCompile\circularocli.exe users change-password <id> --new "DemoSecr3t!" --current "OldSecr3t"

# 11. Check password strength policy
.\build\native\nativeCompile\circularocli.exe users check-password --password "weakpass" --tenant "mytenant"

# 12. Upload a profile photo (Requires a valid photo.jpg)
.\build\native\nativeCompile\circularocli.exe users upload-photo <id> --file ./photo.jpg

# 13. Delete a user's profile photo
.\build\native\nativeCompile\circularocli.exe users delete-photo <id>

# 14. Delete a user completely
.\build\native\nativeCompile\circularocli.exe users delete <id>
```

### Documents Domain
```powershell
# 1. Basic document search (Default table format)
.\build\native\nativeCompile\circularocli.exe documents search

# 2. Exact match search for a document title
.\build\native\nativeCompile\circularocli.exe documents search -q 'title:"Circularo User Guide.pdf"'

# 3. Wildcard search for documents starting with "Invoice"
.\build\native\nativeCompile\circularocli.exe documents search -q "title:Invoice*"

# 4. Search by document status (e.g., signed, draft)
.\build\native\nativeCompile\circularocli.exe documents search -q "status:signed"

# 5. Combined search (signed documents containing "contract" in title)
.\build\native\nativeCompile\circularocli.exe documents search -q "status:signed AND title:*contract*"

# 6. Combined search for user "Lubo"
.\build\native\nativeCompile\circularocli.exe users search -q "+fullname:*ubo*"

# 7. Fetch 10 documents and format as JSON
.\build\native\nativeCompile\circularocli.exe documents search --limit 10 -f json
```

### Reports Domain (Workflow Tracking)
```powershell
# 1. List circulation reports
.\build\native\nativeCompile\circularocli.exe reports search

# 2. Verbose view of reports (Shows full processed and waiting actions)
.\build\native\nativeCompile\circularocli.exe reports search --verbose

# 3. Filter reports by sender
.\build\native\nativeCompile\circularocli.exe reports search -q "sender:Lubos*"

# 4. Filter reports by specific document title
.\build\native\nativeCompile\circularocli.exe reports search -q "title:\"NDA Agreement.pdf\""
```

### Transactions Domain
```powershell
# 1. Search general transactions
.\build\native\nativeCompile\circularocli.exe transactions search

# 2. Search only completed transactions
.\build\native\nativeCompile\circularocli.exe transactions search --state completed

# 3. Search only in-progress transactions
.\build\native\nativeCompile\circularocli.exe transactions search --state inprogress

# 4. Detailed verbose view of completed transactions (Participant workflows)
.\build\native\nativeCompile\circularocli.exe transactions search --state completed --verbose

# 5. Find draft invoices specifically
.\build\native\nativeCompile\circularocli.exe transactions search -q "title:Invoice*" --state draft
```
