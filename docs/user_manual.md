# Circularo CLI User Manual

Welcome! This guide is designed to help you set up and use the **Circularo CLI Search Utility**, even if you are not a programmer or technical user. It provides complete command-line reference guides, sample outputs, troubleshooting help, and exit codes.

---

## 1. What is this tool?
The Circularo CLI is a command-line tool that lets you search and display information about **Users**, **Documents**, **Workflow Reports**, and **Transactions** in your Circularo system without needing to open your web browser. 

The CLI is packaged as a GraalVM native binary (`circularocli.exe`), executing with high performance and minimal startup time (typically **< 0.05 seconds**).

---

## 2. First-Time Setup
Before using the tool, you need to configure your authentication details so it can securely talk to your Circularo server.

1. Locate the file named `.env.example` in the tool's folder.
2. Duplicate (copy and paste) this file, and rename the new copy to `.env`.
3. Open `.env` in any text editor (like Notepad or TextEdit) and fill in your details:
   - `CIRCULARO_API_URL`: The API URL of your tenant (e.g., `https://app.sandbox.circularo.com/api/v1/`).
   - `CIRCULARO_API_TOKEN`: Your personal API security token (which you can generate in your Circularo profile settings page).

Example `.env` file content:
```env
CIRCULARO_API_URL=https://app.sandbox.circularo.com/api/v1/
CIRCULARO_API_TOKEN=abcd232452efgh
```

---

## 3. How to Run the Tool
First, open your command terminal:
- **Windows**: Search for and open **PowerShell** or **Command Prompt**.
- **macOS / Linux**: Open the **Terminal** app.

Navigate to the folder containing the tool and use the commands below.

> [!NOTE]
> **Windows PowerShell Execution Rule:**  
> PowerShell does not load executables from the current directory by default for security reasons. Therefore, if you are running the tool locally from its directory and it is not in your system PATH, you must prefix commands with `.\` (e.g. `.\circularocli.exe`). The examples below use the `.\` prefix. If you have added the folder containing `circularocli.exe` to your system environment variables PATH, you can run the command as `circularocli.exe` directly from any folder.

### Command Help
To view the help tree or command arguments, append `--help` or `-h`:
```powershell
.\circularocli.exe --help
```

### Version Check
```powershell
.\circularocli.exe --version
```
**Expected Output:**
```text
circularo-cli 1.0.0
```

---

## 4. Managing Users (The `users` Command Group)

The `users` command group provides comprehensive tools to search, retrieve, create, update, delete, and configure user accounts in your Circularo tenant.

Running the base command displays all available subcommands:
```powershell
.\circularocli.exe users
```

### 4.1 Search & Retrieval

#### Search Users (`search`)
Queries the Circularo users database using Elasticsearch query_string syntax. If no query is provided, lists all users.
```powershell
# List all users (Table format)
.\circularocli.exe users search

# Search for a specific user name or email
.\circularocli.exe users search -q "Lubo"

# Limit results to 10 records
.\circularocli.exe users search --limit 10

# Export search results to JSON
.\circularocli.exe users search -q "mail:*@seznam.cz" -f json
```

#### Get User by ID (`get`)
Gets complete details for the user with the specified ID.
```powershell
.\circularocli.exe users get <id>
```

#### Get Simple User Info by Username (`get-by-username`)
Retrieves simple user info using the username.
```powershell
.\circularocli.exe users get-by-username <username> --photo-size <large|medium|small>
```

#### Get User Rights (`rights`)
Displays the actions and access rights assigned to a user.
```powershell
.\circularocli.exe users rights <id> --detailed
```

### 4.2 Account CRUD Operations

#### Create Users Bulk (`create`)
Creates one or more new user accounts using a local JSON payload file.
```powershell
.\circularocli.exe users create --file ./new_users.json
```

#### Update Users Bulk (`update`)
Updates properties of multiple users in bulk using a JSON payload file.
```powershell
.\circularocli.exe users update --file ./update_users.json
```

#### Update Single User (`update-single`)
Modifies a single user's properties by ID, with optional active token disabling and optimistic locking versioning.
```powershell
.\circularocli.exe users update-single <id> --file ./user_properties.json --disable-active-tokens --version 1
```

#### Delete User (`delete`)
Deletes the specified user account, with an optional username to reassign their resources to.
```powershell
.\circularocli.exe users delete <id> --reassign <username>
```

#### Register User (`register`)
Self-registers a new user account with default settings.
```powershell
.\circularocli.exe users register --username joesmith --email joe@example.com --fullname "Joe Smith" --phone "+1234567890" --company "Acme Corp" --language "en"
```

### 4.3 Authentication & Security

#### Activate User (`activate`)
Activates a new user account. Requires setting a password. You can optionally upload a local profile photo.
```powershell
.\circularocli.exe users activate --password "MySecret123" --fullname "Lubos" --file ./avatar.png
```

#### Resend Activation Email (`resend-activation`)
Resends the activation link/email to a newly created user.
```powershell
.\circularocli.exe users resend-activation <id> --tenant <tenant-name>
```

#### Check Activation Token (`check-activation`)
Verifies the validity of the current user account activation token.
```powershell
.\circularocli.exe users check-activation
```

#### Change Password (`change-password`)
Changes the password for a specific user ID.
```powershell
.\circularocli.exe users change-password <id> --new "NewSecr3t" --current "OldSecr3t"
```

#### Check Password Strength (`check-password`)
Checks the strength of a proposed password.
```powershell
.\circularocli.exe users check-password --password "weakpass" --tenant <tenant-name>
```

#### Reset Password Request (`reset-password`)
Submits a password reset request with a username, token code, and the new password.
```powershell
.\circularocli.exe users reset-password --username joesmith --code 987654 --password "NewPassword123"
```

#### Request Password Reset Link (`reset-password-username`)
Initiates the password reset flow for a given username.
```powershell
.\circularocli.exe users reset-password-username joesmith --tenant <tenant-name>
```

#### Check Reset Token Validity (`check-reset-token`)
Validates a password reset token.
```powershell
.\circularocli.exe users check-reset-token --token <reset-token>
```

### 4.4 Group Management

#### Add User to Group (`add-group`)
Adds a user to a security/permission group.
```powershell
.\circularocli.exe users add-group <id> --group <groupname>
```

#### Remove User from Group (`remove-group`)
Removes a user from a security/permission group.
```powershell
.\circularocli.exe users remove-group <id> --group <groupname>
```

### 4.5 Profile Assets & Certificates

#### Upload Profile Photo (`upload-photo`)
Uploads a local image file as the user's profile photo.
```powershell
.\circularocli.exe users upload-photo <id> --file ./photo.jpg
```

#### Delete Profile Photo (`delete-photo`)
Deletes the profile photo of a user.
```powershell
.\circularocli.exe users delete-photo <id>
```

#### Delete Signature Image (`delete-signature`)
Deletes the user's saved hand-drawn signature image.
```powershell
.\circularocli.exe users delete-signature <id>
```

#### Delete Initials Image (`delete-initials`)
Deletes the user's saved initials image.
```powershell
.\circularocli.exe users delete-initials <id>
```

#### Add User Certificate (`add-certificate`)
Adds a certificate to a user profile, passing a local certificate file.
```powershell
.\circularocli.exe users add-certificate <id> --name "SigningCert" --type "personal" --file ./cert.p12
```

#### Remove User Certificate (`remove-certificate`)
Removes a certificate from a user profile by certificate ID.
```powershell
.\circularocli.exe users remove-certificate <id> --cert-id=<certificateId>
```

> [!NOTE]
> **Sandbox Limitations on User Queries:**
> The current Circularo Sandbox API backend has strict query constraints. Any non-empty query string passed to `/users/search` (such as `-q "Luboš"` or `-q "mail:klauberl@seznam.cz"`) returns a `400 Bad Request` with "Malformed query." from Elasticsearch. This is an environment/sandbox limitation, but retrieving all users without a query operates successfully.

---

## 5. Searching for Documents (The `documents` Command Group)

The `documents` command group provides access to document search and management operations.

Running the base command displays all available subcommands:
```powershell
.\circularocli.exe documents
```

### Search Documents (`search`)
Queries documents saved in your Circularo repository.

#### Basic command (Lists all documents):
```powershell
.\circularocli.exe documents search
```
**Expected Output (Table Format):**
```text
+--------------------------------------+--------------------------+------+--------------------------+--------+
| Document ID                          | Title                    | Type | Created Date             | Status |
+--------------------------------------+--------------------------+------+--------------------------+--------+
| ba4ae8ed-94e3-4546-a02a-12361cb27c60 | Circularo User Guide.pdf | ext  | 2026-05-18T10:00:41.240Z |        |
+--------------------------------------+--------------------------+------+--------------------------+--------+
```

#### Export to JSON:
```powershell
.\circularocli.exe documents search -f json
```
**Expected Output (JSON Format):**
```json
[ {
  "documentId" : "ba4ae8ed-94e3-4546-a02a-12361cb27c60",
  "documentTitle" : "Circularo User Guide.pdf",
  "type" : "ext",
  "date" : "2026-05-18T10:00:41.240Z",
  "workflow" : {
    "shareStateName" : null
  }
} ]
```

#### Filtering Search Results:
Use the `-q` or `--query` option to filter your documents.

> [!NOTE]
> **Elasticsearch Field Mapping Difference:**
> While the REST API returns the field name as `"type": "ext"` in the output JSON, Elasticsearch indexes this property as **`definitionType`** to avoid collision with the internal metadata `_type` keyword.
> To filter documents by their type, you must query **`definitionType:ext`** (or `definitionType:int`) instead of `type:ext`.

| Goal | Example Command |
| :--- | :--- |
| **Exact Title Search** | `.\circularocli.exe documents search -q 'title:"Circularo User Guide.pdf"'` |
| **Search by Document Type** *(e.g. ext, int)* | `.\circularocli.exe documents search -q "definitionType:ext"` |
| **Wildcard Title Search** *(finds anything starting with Invoice)* | `.\circularocli.exe documents search -q "title:Invoice*"` |
| **Search by Status** *(e.g. signed, draft, inprogress)* | `.\circularocli.exe documents search -q "status:signed"` |
| **Combined Search** *(signed documents with "contract" in title)* | `.\circularocli.exe documents search -q "status:signed AND title:*contract*"` |
| **Limit result count** | `.\circularocli.exe documents search --limit 10` |

---

## 6. Searching for Workflow Reports (The `reports` Command Group)

The `reports` command group provides access to workflow report search and analysis operations.

Running the base command displays all available subcommands:
```powershell
.\circularocli.exe reports
```

### Search Reports (`search`)
Queries the Circularo document workflow database to generate detailed reports about the circulation (oběh) of documents, showing processed, waiting, and total recipient statistics.

#### Basic command (Lists all reports):
```powershell
.\circularocli.exe reports search
```

#### Detailed/Verbose View:
Use the `-v` or `--verbose` flag to output detailed lists of actions already processed (participants who processed the action, when, and action type) and those currently waiting on turn.
```powershell
.\circularocli.exe reports search --verbose
```

#### Filtering Search Results:
Use the `-q` or `--query` option to filter workflow reports by creator, sender, or document title.

| Goal | Example Command |
| :--- | :--- |
| **Search by Document Title** | `.\circularocli.exe reports search -q "title:\"NDA Agreement.pdf\""` |
| **Search by Owner/Creator** | `.\circularocli.exe reports search -q "owner:Lubos*"` |
| **Search by Sender** | `.\circularocli.exe reports search -q "sender:Lubos*"` |
| **Limit result count** | `.\circularocli.exe reports search --limit 10` |

> [!WARNING]
> **Sandbox Authorization Limitations:**
> Standard demo/sandbox environment tokens typically lack the developer export role (`use_all_documents_export`). Consequently, running either `reports search` or `transactions search` against the real API returns an `Authorization Error (403 Forbidden)` (detailed in Section 9).

---

## 7. Searching and Filtering Transactions (The `transactions` Command Group)

The `transactions` command group is tailored to inspect and track specific document workflow transactions, including start dates, end dates, duration, reasons, and transaction state.

Running the base command displays all available subcommands:
```powershell
.\circularocli.exe transactions
```

### Search Transactions (`search`)

#### Basic command (Lists all transactions):
```powershell
.\circularocli.exe transactions search
```

#### Detailed/Verbose View:
Use the `-v` or `--verbose` flag to inspect detailed participant workflow paths for transactions:
```powershell
.\circularocli.exe transactions search --state completed --verbose
```

#### Filtering by Transaction State:
Use the `-s` or `--state` option to filter results by specific transaction states (e.g. `completed`, `inprogress`, `draft`, `cancelled`).

| Goal | Example Command |
| :--- | :--- |
| **List Completed Transactions** | `.\circularocli.exe transactions search --state completed` |
| **List In-Progress Transactions** | `.\circularocli.exe transactions search --state inprogress` |
| **Search and Filter by State** *(draft invoices)* | `.\circularocli.exe transactions search -q "title:Invoice*" --state draft` |
| **Limit result count** | `.\circularocli.exe transactions search --limit 25` |

---

## 8. Formatting Outputs (Table vs JSON vs Markdown)

The tool supports three output formats:
1. **Table** (default): Human-readable ASCII grids styled for the terminal window.
2. **JSON**: Structured, machine-readable pretty-printed JSON representation (perfect for scripting and API integrations).
3. **Markdown**: Standard GFM (GitHub Flavored Markdown) tables, perfect for copying directly into GitHub pull requests, READMEs, or documentation.

### Example: Show users in human-readable table:
```powershell
.\circularocli.exe users search --format table
```

> [!NOTE]
> The top-level `users` command is a dispatch group and does not accept `--format` directly.
> Always use `users search` (or another subcommand) when specifying output format.

### Example: Export users to a JSON text block:
```powershell
.\circularocli.exe users search --format json
```

### Example: Output documents in JSON format:
```powershell
.\circularocli.exe documents search --format json
```

### Example: Output reports in GFM Markdown format:
```powershell
.\circularocli.exe reports search --format md
```

---

## 9. Troubleshooting & Error Code Showcase

This section details how the CLI handles and formats errors, displaying user-friendly, descriptive messages with corresponding exit codes instead of printing Java stack traces.

### Scenario A: Missing Credentials (Local Configuration Error)
* **Trigger:** Running an API-communicating command when `.env` is missing or contains empty values.
* **Command:**
  ```powershell
  # (Temporarily renaming .env triggers this)
  .\circularocli.exe users search
  ```
* **CLI Output (Stderr):**
  ```text
  Error: API token not found. Please configure a '.env' file next to the executable, set the CIRCULARO_API_TOKEN environment variable, or configure ~/.circularo/config.properties
  ```
* **Exit Code:** `1`

### Scenario B: Authorization Failure (HTTP `403 Forbidden`)
* **Trigger:** Requesting a resource that requires rights not granted to your API token.
* **Command:**
  ```powershell
  .\circularocli.exe reports search
  ```
* **CLI Output (Stderr):**
  ```text
  Authorization Error: API request failed with status code 403. Response: {"status":403,"type":"STATUS:FORBIDDEN","message":"User is not allowed to use this resource. Required rights: \"use_all_documents_export\"","requestId":"01b36fad-ac61-412f-94e1-aede3635d578"}
  ```
* **Exit Code:** `1`

### Scenario C: Malformed Input (HTTP `400 Bad Request`)
* **Trigger:** Sending a search string format that is rejected by the server index parser.
* **Command:**
  ```powershell
  .\circularocli.exe users search -q "Luboš"
  ```
* **CLI Output (Stderr):**
  ```text
  Validation Error: API request failed with status code 400. Response: {"status":400,"type":"STATUS:BAD_REQUEST","message":"Malformed query.","requestId":"6b980dc1-c165-4ea8-a668-b051469ce14d"}
  ```
* **Exit Code:** `1`

### Scenario D: Connection Refused / Invalid Base URL (Network / Routing Error)
* **Trigger:** An incorrect `CIRCULARO_API_URL` value inside `.env` or network outage.
* **Command:**
  ```powershell
  # (Simulated with invalid URL)
  .\circularocli.exe users search
  ```
* **CLI Output (Stderr):**
  ```text
  Error: Network error occurred during Circularo API call: Connect to incorrect-domain.com:443 failed: Host is unresolved
  ```
* **Exit Code:** `1`
