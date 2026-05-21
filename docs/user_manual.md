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

### Command Help
To view the help tree or command arguments, append `--help` or `-h`:
```powershell
.\build\native\nativeCompile\circularocli.exe --help
```

### Version Check
```powershell
.\build\native\nativeCompile\circularocli.exe --version
```
**Expected Output:**
```text
circularo-cli 1.0.0
```

---

## 4. Searching for Users (The `users` Command)

The `users` command searches for user accounts inside your Circularo organization.

### Basic command (Lists all users):
```powershell
.\build\native\nativeCompile\circularocli.exe users
```
**Expected Output (Table Format):**
```text
+--------------------+-----------+------+--------+----------+
| ID/Email           | Full Name | Role | Status | Type     |
+--------------------+-----------+------+--------+----------+
| klauberl@seznam.cz | Luboš     |      |        | internal |
+--------------------+-----------+------+--------+----------+
```

### Export to JSON:
```powershell
.\build\native\nativeCompile\circularocli.exe users -f json
```
**Expected Output (JSON Format):**
```json
[ {
  "id" : null,
  "fullname" : "Luboš",
  "mail" : "klauberl@seznam.cz",
  "name" : "klauberl@seznam.cz",
  "phone" : "+420603700156",
  "recordType" : "internal",
  "role" : null,
  "status" : null
} ]
```

### Filtering Search Results:
To search for a specific person or attribute, use the `-q` or `--query` option. 

> [!IMPORTANT]
> **Shell Quoting Rule:** Always wrap your query inside double quotes or single quotes `"..."` so the command terminal passes your search correctly.

| Goal | Example Command |
| :--- | :--- |
| **Simple Name Search** | `circularo-cli users -q "Lubo"` |
| **Search by exact Email** | `circularo-cli users -q "mail:klauberl@seznam.cz"` |
| **Wildcard Email Search** *(any address ending in a domain)* | `circularo-cli users -q "mail:*@seznam.cz"` |
| **Search by Phone Number** | `circularo-cli users -q "phone:+420700600500"` |
| **Combined Search** *(Internal users matching a domain)* | `circularo-cli users -q "recordType:internal AND mail:*seznam*"` |

> [!NOTE]
> **Sandbox Limitations on User Queries:**
> The current Circularo Sandbox API backend has strict query constraints. Any non-empty query string passed to `/users/search` (such as `-q "Luboš"` or `-q "mail:klauberl@seznam.cz"`) returns a `400 Bad Request` with "Malformed query." from Elasticsearch. This is an environment/sandbox limitation, but retrieving all users without a query operates successfully.

---

## 5. Searching for Documents (The `documents` Command)

The `documents` command queries documents saved in your Circularo repository.

### Basic command (Lists all documents):
```powershell
.\build\native\nativeCompile\circularocli.exe documents
```
**Expected Output (Table Format):**
```text
+--------------------------------------+--------------------------+------+--------------------------+--------+
| Document ID                          | Title                    | Type | Created Date             | Status |
+--------------------------------------+--------------------------+------+--------------------------+--------+
| ba4ae8ed-94e3-4546-a02a-12361cb27c60 | Circularo User Guide.pdf | ext  | 2026-05-18T10:00:41.240Z |        |
+--------------------------------------+--------------------------+------+--------------------------+--------+
```

### Export to JSON:
```powershell
.\build\native\nativeCompile\circularocli.exe documents -f json
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

### Filtering Search Results:
Use the `-q` or `--query` option to filter your documents.

> [!NOTE]
> **Elasticsearch Field Mapping Difference:**
> While the REST API returns the field name as `"type": "ext"` in the output JSON, Elasticsearch indexes this property as **`definitionType`** to avoid collision with the internal metadata `_type` keyword.
> To filter documents by their type, you must query **`definitionType:ext`** (or `definitionType:int`) instead of `type:ext`.

| Goal | Example Command |
| :--- | :--- |
| **Exact Title Search** | `circularo-cli documents -q 'title:"Circularo User Guide.pdf"'` |
| **Search by Document Type** *(e.g. ext, int)* | `circularo-cli documents -q "definitionType:ext"` |
| **Wildcard Title Search** *(finds anything starting with Invoice)* | `circularo-cli documents -q "title:Invoice*"` |
| **Search by Status** *(e.g. signed, draft, inprogress)* | `circularo-cli documents -q "status:signed"` |
| **Combined Search** *(signed documents with "contract" in title)* | `circularo-cli documents -q "status:signed AND title:*contract*"` |

---

## 6. Searching for Workflow Reports (The `reports` Command)

The `reports` command queries the Circularo document workflow database to generate detailed reports about the circulation (oběh) of documents, showing processed, waiting, and total recipient statistics.

### Basic command (Lists all reports):
```powershell
.\build\native\nativeCompile\circularocli.exe reports
```

### Detailed/Verbose View:
Use the `-v` or `--verbose` flag to output detailed lists of actions already processed (participants who processed the action, when, and action type) and those currently waiting on turn.
```powershell
.\build\native\nativeCompile\circularocli.exe reports --verbose
```

### Filtering Search Results:
Use the `-q` or `--query` option to filter workflow reports by creator, sender, or document title.

| Goal | Example Command |
| :--- | :--- |
| **Search by Document Title** | `circularo-cli reports -q "title:\"NDA Agreement.pdf\""` |
| **Search by Owner/Creator** | `circularo-cli reports -q "owner:Lubos*"` |
| **Search by Sender** | `circularo-cli reports -q "sender:Lubos*"` |

> [!WARNING]
> **Sandbox Authorization Limitations:**
> Standard demo/sandbox environment tokens typically lack the developer export role (`use_all_documents_export`). Consequently, running either `reports` or `transactions` against the real API returns an `Authorization Error (403 Forbidden)` (detailed in Section 9).

---

## 7. Searching and Filtering Transactions (The `transactions` Command)

The `transactions` command is tailored to inspect and track specific document workflow transactions, including start dates, end dates, duration, reasons, and transaction state.

### Basic command (Lists all transactions):
```powershell
.\build\native\nativeCompile\circularocli.exe transactions
```

### Detailed/Verbose View:
Use the `-v` or `--verbose` flag to inspect detailed participant workflow paths for transactions:
```powershell
.\build\native\nativeCompile\circularocli.exe transactions --state completed --verbose
```

### Filtering by Transaction State:
Use the `-s` or `--state` option to filter results by specific transaction states (e.g. `completed`, `inprogress`, `draft`, `cancelled`).

| Goal | Example Command |
| :--- | :--- |
| **List Completed Transactions** | `circularo-cli transactions --state completed` |
| **List In-Progress Transactions** | `circularo-cli transactions --state inprogress` |
| **Search and Filter by State** *(draft invoices)* | `circularo-cli transactions -q "title:Invoice*" --state draft` |

---

## 8. Formatting Outputs (Table vs JSON vs Markdown)

The tool supports three output formats:
1. **Table** (default): Human-readable ASCII grids styled for the terminal window.
2. **JSON**: Structured, machine-readable pretty-printed JSON representation (perfect for scripting and API integrations).
3. **Markdown**: Standard GFM (GitHub Flavored Markdown) tables, perfect for copying directly into GitHub pull requests, READMEs, or documentation.

### Example: Show users in human-readable table:
```powershell
.\build\native\nativeCompile\circularocli.exe users --format table
```

### Example: Export users to a JSON text block:
```powershell
.\build\native\nativeCompile\circularocli.exe users --format json
```

### Example: Output users in GFM Markdown format:
```powershell
.\build\native\nativeCompile\circularocli.exe users --format md
```

---

## 9. Troubleshooting & Error Code Showcase

This section details how the CLI handles and formats errors, displaying user-friendly, descriptive messages with corresponding exit codes instead of printing Java stack traces.

### Scenario A: Missing Credentials (Local Configuration Error)
* **Trigger:** Running a command when `.env` is missing or contains empty values.
* **Command:**
  ```powershell
  # (Temporarily renaming .env triggers this)
  .\build\native\nativeCompile\circularocli.exe users
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
  .\build\native\nativeCompile\circularocli.exe reports
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
  .\build\native\nativeCompile\circularocli.exe users -q "Luboš"
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
  .\build\native\nativeCompile\circularocli.exe users
  ```
* **CLI Output (Stderr):**
  ```text
  Error: Network error occurred during Circularo API call: Connect to incorrect-domain.com:443 failed: Host is unresolved
  ```
* **Exit Code:** `1`
