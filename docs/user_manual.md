# Circularo CLI User Manual

Welcome! This guide is designed to help you set up and use the **Circularo CLI Search Utility**, even if you are not a programmer or technical user. 

---

## 1. What is this tool?
The Circularo CLI is a command-line tool that lets you search and display information about **Users** and **Documents** in your Circularo system without needing to open your web browser. It displays results as neat tables or structured text.

---

## 2. First-Time Setup
Before using the tool, you need to configure your authentication details so it can securely talk to your Circularo server.

1. Locate the file named `.env.example` in the tool's folder.
2. Duplicate (copy and paste) this file, and rename the new copy to `.env`.
3. Open `.env` in any text editor (like Notepad or TextEdit) and fill in your details:
   - `CIRCULARO_API_URL`: The API URL of your tenant (e.g., `https://app.sandbox.circularo.com/api/v1/`).
   - `CIRCULARO_API_TOKEN`: Your personal API security token. (You can generate this token in your Circularo profile settings page).

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

### Running the Standalone Executable (Native Binary)
If you built the native compiled version, you don't need Java installed:
- **Windows**:
  ```powershell
  .\build\native\nativeCompile\circularocli.exe --help
  ```
- **macOS / Linux**:
  ```bash
  ./build/native/nativeCompile/circularo-cli --help
  ```

### Running with Java (JAR File)
If you are running the standard Java version:
```bash
java -jar build/libs/circularo-cli-0.0.1-SNAPSHOT.jar --help
```

---

## 4. Searching for Users (The `users` Command)

The `users` command searches for user accounts inside your Circularo organization.

### Basic command (Lists all users):
```powershell
circularo-cli users
```

### Filtering Search Results:
To search for a specific person or attribute, use the `-q` or `--query` option. 

> [!IMPORTANT]
> **Shell Quoting Rule:** Always wrap your query inside double quotes `"..."` so the command terminal passes your search correctly.

| Goal | Example Command |
| :--- | :--- |
| **Simple Name Search** | `circularo-cli users -q "Lubo"` |
| **Search by exact Email** | `circularo-cli users -q "mail:klauberl@seznam.cz"` |
| **Wildcard Email Search** *(any address ending in a domain)* | `circularo-cli users -q "mail:*@seznam.cz"` |
| **Search by Phone Number** | `circularo-cli users -q "phone:+420700600500"` |
| **Combined Search** *(Internal users matching a domain)* | `circularo-cli users -q "recordType:internal AND mail:*seznam*"` |

---

## 5. Searching for Documents (The `documents` Command)

The `documents` command queries documents saved in your Circularo repository.

### Basic command (Lists all documents):
```powershell
circularo-cli documents
```

### Filtering Search Results:
Use the `-q` or `--query` option to filter your documents.

| Goal | Example Command |
| :--- | :--- |
| **Exact Title Search** | `circularo-cli documents -q "title:\"NDA Agreement.pdf\""` |
| **Wildcard Title Search** *(finds anything starting with Invoice)* | `circularo-cli documents -q "title:Invoice*"` |
| **Search by Status** *(e.g. signed, draft, inprogress)* | `circularo-cli documents -q "status:signed"` |
| **Combined Search** *(signed documents with "contract" in title)* | `circularo-cli documents -q "status:signed AND title:*contract*"` |

---

## 6. Searching for Workflow Reports (The `reports` Command)

The `reports` command queries the Circularo document workflow database to generate detailed reports about the oběh (circulation) of documents, showing processed, waiting, and total recipient statistics.

### Basic command (Lists all reports):
```powershell
circularo-cli reports
```

### Filtering Search Results:
Use the `-q` or `--query` option to filter workflow reports by creator, sender, or document title.

| Goal | Example Command |
| :--- | :--- |
| **Search by Document Title** | `circularo-cli reports -q "title:\"NDA Agreement.pdf\""` |
| **Search by Owner/Creator** | `circularo-cli reports -q "owner:Lubos*"` |
| **Search by Sender** | `circularo-cli reports -q "sender:Lubos*"` |
| **Combined Search** *(reports owned and sent by Lubos)* | `circularo-cli reports -q "owner:Lubos* AND sender:Lubos*"` |

---

## 7. Searching and Filtering Transactions (The `transactions` Command)

The `transactions` command is tailored to inspect and track specific document workflow transactions, including start dates, end dates, duration, reasons, and transaction state.

### Basic command (Lists all transactions):
```powershell
circularo-cli transactions
```

### Filtering by Transaction State:
Use the `-s` or `--state` option to filter results by specific transaction states (e.g. `completed`, `inprogress`, `draft`, `cancelled`).

| Goal | Example Command |
| :--- | :--- |
| **List Completed Transactions** | `circularo-cli transactions --state completed` |
| **List In-Progress Transactions** | `circularo-cli transactions --state inprogress` |
| **Search and Filter by State** *(draft invoices)* | `circularo-cli transactions -q "title:Invoice*" --state draft` |

---

## 8. Formatting Outputs (Tables vs JSON)

By default, the tool displays results in a **Table** grid which is easy for humans to read. 

However, if you want to export the data or use it inside other scripts, you can specify the **JSON** output format using the `-f json` or `--format json` option.

### Example: Show users in human-readable table:
```powershell
circularo-cli users --format table
```

### Example: Export users to a JSON text block:
```powershell
circularo-cli users --format json
```

---

## 9. Troubleshooting / Common Problems

### ❌ Error: "Invalid credentials" or HTTP status `401`
* **What it means:** The security token you provided in `.env` is either expired, incorrect, or missing.
* **How to fix:** Check your `.env` file and make sure the `CIRCULARO_API_TOKEN` matches your token from the Circularo user portal exactly.

### ❌ Error: "Connection refused" or HTTP status `404` / `500`
* **What it means:** The tool cannot reach the Circularo API server.
* **How to fix:** Check the `CIRCULARO_API_URL` line in `.env`. Ensure it starts with `https://`, ends with `/api/v1/`, and contains no typos. Also check your internet connection.

### ❌ Error: "Malformed query" or HTTP status `400`
* **What it means:** The search query syntax you passed inside the `-q` or `--query` option is invalid.
* **How to fix:** Make sure you have wrapped the entire query in quotes (e.g., `-q "title:Invoice"`). Avoid using invalid characters or unclosed quotes inside the search term.
