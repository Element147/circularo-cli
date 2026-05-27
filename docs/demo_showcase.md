# Circularo CLI - Complete Step-by-Step Beginner Guide

Welcome! This guide is designed for **complete beginners** who want to use the Circularo Command Line Interface (CLI) to search and manage users, documents, workflow reports, and transactions. 

You do **not** need to know how programming works, how Java works, or how to write code. Simply follow these step-by-step instructions.

---

> [!IMPORTANT]
> **No Java Required!**
> Because our automated system on GitHub pre-compiles the application into a standalone native program, **you do not need to install Java, Java Virtual Machine (JVM), or any software development tools** on your computer. The app runs completely on its own.

---

## Step 1: Download the Application

1. Open your web browser and go to this link:  
   👉 [GitHub Build Artifacts Page](https://github.com/Element147/circularo-cli/actions/runs/26445127835)
2. Scroll down to the very bottom of the page to find the section called **Artifacts** (these are the prebuilt files).
3. Download the file corresponding to your operating system:
   * **Windows users:** Click and download **`circularocli-windows-x86_64`** (this is a `.zip` file).
   * **Linux users:** Click and download **`circularocli-linux-x86_64`** (this is a `.zip` file).
4. Locate the downloaded `.zip` file on your computer and extract it:
   * **Windows:** Right-click the `.zip` file, select **Extract All...**, and choose a simple location like `C:\circularo-cli`.
   * **Linux:** Extract it using your archive manager or terminal to a folder like `~/circularo-cli`.

---

## Step 2: Set Up Your Configuration & Security Token

Before the application can talk to your Circularo server, it needs to know your server address and your secure login key (security token).

1. In the folder where you extracted the app, you will find a file named `.env.example`.
2. **Duplicate/Copy** this file and rename the new copy to exactly **`.env`** (make sure there is a dot `.` at the beginning and no `.txt` extension at the end).
3. Open this new `.env` file in a simple text editor:
   * **Windows:** Right-click `.env`, select **Open with**, and choose **Notepad**.
   * **Linux:** Open it with any text editor (like **Text Editor**, **Gedit**, or **Nano**).
4. Fill in your details:
   * `CIRCULARO_API_URL`: The web address of your Circularo server API. (Example: `https://app.sandbox.circularo.com/api/v1/`)
   * `CIRCULARO_API_TOKEN`: Your private security key. You can get this by logging into the Circularo website, going to your Profile settings page, and generating an API Token.
5. Save the file and close your editor.

Your `.env` file should look like this:
```env
CIRCULARO_API_URL=https://app.sandbox.circularo.com/api/v1/
CIRCULARO_API_TOKEN=your_private_security_token_here
```

---

## Step 3: Open Your Terminal & Navigate to the App Folder

To run the commands, you need to open your computer's command terminal.

### 💻 Windows
1. Press the **Windows Key** on your keyboard, type **PowerShell**, and press **Enter**.
2. Type the following command to move into the app folder (assuming you extracted it to `C:\circularo-cli`) and press **Enter**:
   ```powershell
   cd C:\circularo-cli
   ```

### 🐧 Linux
1. Open the **Terminal** app from your system application menu.
2. Type the following command to move into the app folder (assuming you extracted it to `~/circularo-cli`) and press **Enter**:
   ```bash
   cd ~/circularo-cli
   ```

---

## Step 4: Verification and First Commands

Let's test if the application is running correctly. 

### 1. Show Version Information
This command displays the version of the tool you have installed.

* **Windows (PowerShell):**
  ```powershell
  .\circularocli.exe --version
  ```
* **Linux (Terminal):**
  ```bash
  ./circularocli --version
  ```

**Expected Output:**
```text
circularo-cli 1.0.0
```

### 2. Show Help Menu
This command displays list of all available command groups.

* **Windows (PowerShell):**
  ```powershell
  .\circularocli.exe --help
  ```
* **Linux (Terminal):**
  ```bash
  ./circularocli --help
  ```

---

## Step 5: Walkthrough - Executing Commands

Here is the complete step-by-step walkthrough of all operations you can perform. For every command, we provide both the **Windows** version and the **Linux** version.

---

### A. Managing Users (`users` Group)

The `users` commands let you search, create, update, delete, and configure user accounts.

#### 1. List All Users
Displays a table of all user accounts in your system.

* **Windows:**
  ```powershell
  .\circularocli.exe users search
  ```
* **Linux:**
  ```bash
  ./circularocli users search
  ```

#### 2. Search for a User by Name or Email
Finds a specific user by name or email query.

* **Windows:**
  ```powershell
  .\circularocli.exe users search -q "Lubo"
  ```
* **Linux:**
  ```bash
  ./circularocli users search -q "Lubo"
  ```

#### 3. Limit Search Results
Only returns a specific number of records (e.g., 3 records).

* **Windows:**
  ```powershell
  .\circularocli.exe users search --limit 3
  ```
* **Linux:**
  ```bash
  ./circularocli users search --limit 3
  ```

#### 4. Export User Details to JSON Text Block
Produces raw machine-readable JSON instead of a formatted table.

* **Windows:**
  ```powershell
  .\circularocli.exe users search -q "mail:*@seznam.cz" -f json
  ```
* **Linux:**
  ```bash
  ./circularocli users search -q "mail:*@seznam.cz" -f json
  ```

#### 5. Get Detailed Info for a Specific User
Retrieves the complete profile data using the user's email address. Let's assume you found a user with email `joe.smith@circularo.com` from your search.

* **Windows:**
  ```powershell
  .\circularocli.exe users get joe.smith@circularo.com
  ```
* **Linux:**
  ```bash
  ./circularocli users get joe.smith@circularo.com
  ```

#### 6. Get User Info by Username
Finds basic info by username.

* **Windows:**
  ```powershell
  .\circularocli.exe users get-by-username "joesmith"
  ```
* **Linux:**
  ```bash
  ./circularocli users get-by-username "joesmith"
  ```

#### 7. View User Rights / Permissions
Shows the list of allowed actions assigned to a user account.

* **Windows:**
  ```powershell
  .\circularocli.exe users rights joe.smith@circularo.com --detailed
  ```
* **Linux:**
  ```bash
  ./circularocli users rights joe.smith@circularo.com --detailed
  ```

#### 8. Create Multiple Users in Bulk
Loads a local file containing user parameters and creates them. *(Make sure you have a `new_users.json` file in your folder).*

* **Windows:**
  ```powershell
  .\circularocli.exe users create --file ./new_users.json
  ```
* **Linux:**
  ```bash
  ./circularocli users create --file ./new_users.json
  ```

#### 9. Update Multiple Users in Bulk
Loads an update payload file to update several accounts simultaneously. *(Make sure you have an `update_users.json` file in your folder).*

* **Windows:**
  ```powershell
  .\circularocli.exe users update --file ./update_users.json
  ```
* **Linux:**
  ```bash
  ./circularocli users update --file ./update_users.json
  ```

#### 10. Update a Single User
Updates properties for a single user, with advanced options like disabling old active login tokens.

* **Windows:**
  ```powershell
  .\circularocli.exe users update-single joe.smith@circularo.com --file ./user_properties.json --disable-active-tokens --version 1
  ```
* **Linux:**
  ```bash
  ./circularocli users update-single joe.smith@circularo.com --file ./user_properties.json --disable-active-tokens --version 1
  ```

#### 11. Self-Register a User Account
Registers a new user profile.

* **Windows:**
  ```powershell
  .\circularocli.exe users register --username joesmith --email joe@example.com --fullname "Joe Smith" --phone "+1234567890" --company "Acme Corp" --language "en"
  ```
* **Linux:**
  ```bash
  ./circularocli users register --username joesmith --email joe@example.com --fullname "Joe Smith" --phone "+1234567890" --company "Acme Corp" --language "en"
  ```

#### 12. Activate a User Account
Activates a user using a default password.

* **Windows:**
  ```powershell
  .\circularocli.exe users activate --password "MySecret123" --fullname "Lubos" --file ./avatar.png
  ```
* **Linux:**
  ```bash
  ./circularocli users activate --password "MySecret123" --fullname "Lubos" --file ./avatar.png
  ```

#### 13. Resend Activation Email
Triggers the server to resend the activation link to a specific user.

* **Windows:**
  ```powershell
  .\circularocli.exe users resend-activation joe.smith@circularo.com --tenant "mytenant"
  ```
* **Linux:**
  ```bash
  ./circularocli users resend-activation joe.smith@circularo.com --tenant "mytenant"
  ```

#### 14. Check Activation Token
Verifies whether your activation link or token is still valid.

* **Windows:**
  ```powershell
  .\circularocli.exe users check-activation
  ```
* **Linux:**
  ```bash
  ./circularocli users check-activation
  ```

#### 15. Change Password
Modifies the login password for a user.

* **Windows:**
  ```powershell
  .\circularocli.exe users change-password joe.smith@circularo.com --new "DemoSecr3t!" --current "OldSecr3t"
  ```
* **Linux:**
  ```bash
  ./circularocli users change-password joe.smith@circularo.com --new "DemoSecr3t!" --current "OldSecr3t"
  ```

#### 16. Check Password Strength Policy
Validates whether a proposed password matches your security strength rules.

* **Windows:**
  ```powershell
  .\circularocli.exe users check-password --password "weakpass" --tenant "mytenant"
  ```
* **Linux:**
  ```bash
  ./circularocli users check-password --password "weakpass" --tenant "mytenant"
  ```

#### 17. Add a User to a Permission Group
Grants the user privileges belonging to a specific group.

* **Windows:**
  ```powershell
  .\circularocli.exe users add-group joe.smith@circularo.com --group "Administrators"
  ```
* **Linux:**
  ```bash
  ./circularocli users add-group joe.smith@circularo.com --group "Administrators"
  ```

#### 18. Remove a User from a Permission Group
Revokes privileges belonging to a specific group.

* **Windows:**
  ```powershell
  .\circularocli.exe users remove-group joe.smith@circularo.com --group "Administrators"
  ```
* **Linux:**
  ```bash
  ./circularocli users remove-group joe.smith@circularo.com --group "Administrators"
  ```

#### 19. Upload a Profile Photo
Uploads an image file as your profile photo. *(Make sure you have a `photo.jpg` in your folder).*

* **Windows:**
  ```powershell
  .\circularocli.exe users upload-photo joe.smith@circularo.com --file ./photo.jpg
  ```
* **Linux:**
  ```bash
  ./circularocli users upload-photo joe.smith@circularo.com --file ./photo.jpg
  ```

#### 20. Delete Profile Photo
Deletes your profile photo.

* **Windows:**
  ```powershell
  .\circularocli.exe users delete-photo joe.smith@circularo.com
  ```
* **Linux:**
  ```bash
  ./circularocli users delete-photo joe.smith@circularo.com
  ```

#### 21. Delete User Account Completely
Deletes the user, optionally reassigning their existing documents/resources to another account username.

* **Windows:**
  ```powershell
  .\circularocli.exe users delete joe.smith@circularo.com --reassign "adminsmith"
  ```
* **Linux:**
  ```bash
  ./circularocli users delete joe.smith@circularo.com --reassign "adminsmith"
  ```

---

### B. Searching Documents (`documents` Group)

The `documents` commands let you query and locate PDF files, signing contracts, and text materials in the Circularo database.

#### 1. Show All Documents
Lists every document you have access to.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search
  ```
* **Linux:**
  ```bash
  ./circularocli documents search
  ```

#### 2. Search for a Specific Document by Title
Matches the exact title you specify.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search -q 'title:"Circularo User Guide.pdf"'
  ```
* **Linux:**
  ```bash
  ./circularocli documents search -q 'title:"Circularo User Guide.pdf"'
  ```

#### 3. Wildcard Document Search
Finds all documents whose titles start with "Invoice" followed by any characters.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search -q "title:Invoice*"
  ```
* **Linux:**
  ```bash
  ./circularocli documents search -q "title:Invoice*"
  ```

#### 4. Search by Document Status
Locates documents that are in a particular state, such as `signed` or `draft`.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search -q "status:signed"
  ```
* **Linux:**
  ```bash
  ./circularocli documents search -q "status:signed"
  ```

#### 5. Search for signed contracts containing "NDA"
Executes a multi-property search.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search -q "status:signed AND title:*NDA*"
  ```
* **Linux:**
  ```bash
  ./circularocli documents search -q "status:signed AND title:*NDA*"
  ```

#### 6. Export 10 Documents as JSON
Retrieves a JSON-formatted list of 10 documents.

* **Windows:**
  ```powershell
  .\circularocli.exe documents search --limit 10 -f json
  ```
* **Linux:**
  ```bash
  ./circularocli documents search --limit 10 -f json
  ```

---

### C. Workflow Reports (`reports` Group)

The `reports` command group displays information about the progress (circulation / oběh) of documents through their signature routes.

#### 1. List All Workflow Reports
Shows reports for all active circulations.

* **Windows:**
  ```powershell
  .\circularocli.exe reports search
  ```
* **Linux:**
  ```bash
  ./circularocli reports search
  ```

#### 2. Verbose Workflow Tracking Mode
Using the `--verbose` flag shows detailed steps: which user has already signed the document, the exact time they signed it, and who is currently next in line.

* **Windows:**
  ```powershell
  .\circularocli.exe reports search --verbose
  ```
* **Linux:**
  ```bash
  ./circularocli reports search --verbose
  ```

#### 3. Filter Reports by Sender Name
Locates workflows started by a specific sender.

* **Windows:**
  ```powershell
  .\circularocli.exe reports search -q "sender:Lubos*"
  ```
* **Linux:**
  ```bash
  ./circularocli reports search -q "sender:Lubos*"
  ```

#### 4. Filter Reports by Document Title
Shows details for a single document workflow.

* **Windows:**
  ```powershell
  .\circularocli.exe reports search -q "title:\"NDA Agreement.pdf\""
  ```
* **Linux:**
  ```bash
  ./circularocli reports search -q "title:\"NDA Agreement.pdf\""
  ```

---

### D. Tracking Transactions (`transactions` Group)

The `transactions` command lets you inspect historical events, action logs, participant routes, dates, and execution states.

#### 1. List All Transactions
Displays recent transactions.

* **Windows:**
  ```powershell
  .\circularocli.exe transactions search
  ```
* **Linux:**
  ```bash
  ./circularocli transactions search
  ```

#### 2. Search Only Completed Transactions
Filters the list to completed actions.

* **Windows:**
  ```powershell
  .\circularocli.exe transactions search --state completed
  ```
* **Linux:**
  ```bash
  ./circularocli transactions search --state completed
  ```

#### 3. Search Only In-Progress Transactions
Shows current, active signature operations.

* **Windows:**
  ```powershell
  .\circularocli.exe transactions search --state inprogress
  ```
* **Linux:**
  ```bash
  ./circularocli transactions search --state inprogress
  ```

#### 4. Verbose Transaction Review
Shows the exhaustive workflow step-by-step history of participant actions for completed transactions.

* **Windows:**
  ```powershell
  .\circularocli.exe transactions search --state completed --verbose
  ```
* **Linux:**
  ```bash
  ./circularocli transactions search --state completed --verbose
  ```

---

## Step 6: Formatting Outputs

You can easily customize how information is displayed by appending the `--format` (or `-f`) option followed by `table`, `json`, or `md`.

### Option A: Human-Readable Table (Default)
Draws a visual text table inside your terminal window.
* **Windows Example:** `.\circularocli.exe users search --format table`
* **Linux Example:** `./circularocli users search --format table`

### Option B: Machine-Readable JSON
Outputs raw data, perfect for copying into other applications or scripts.
* **Windows Example:** `.\circularocli.exe users search --format json`
* **Linux Example:** `./circularocli users search --format json`

### Option C: GitHub Markdown (GFM)
Draws a beautifully structured markdown table. You can copy and paste this direct into GitHub, GitLab, Notion, or Markdown notes.
* **Windows Example:** `.\circularocli.exe users search --format md`
* **Linux Example:** `./circularocli users search --format md`

---

## Step 7: Troubleshooting and Easy Explanations

Here are the most common things that can go wrong, what the terminal will print, and exactly how to fix it in 5 seconds.

### 1. Error: "API token not found..."
* **What you see:**
  ```text
  Error: API token not found. Please configure a '.env' file next to the executable, set the CIRCULARO_API_TOKEN environment variable, or configure ~/.circularo/config.properties
  ```
* **What it means:** The app cannot find your security token key.
* **How to fix it:** Go back to **Step 2**. Ensure your `.env` file is named exactly `.env` (not `.env.example` or `.env.txt`) and is in the same folder as the app executable file.

### 2. Error: "Authorization Error: API request failed with status code 403..."
* **What you see:**
  ```text
  Authorization Error: API request failed with status code 403. Response: {"status":403,"type":"STATUS:FORBIDDEN","message":"User is not allowed to use this resource. Required rights: \"use_all_documents_export\""...}
  ```
* **What it means:** Your Circularo API Token does not have permission to access that specific information.
* **How to fix it:** Log in to the Circularo web portal, go to your User Profile / Security settings, and make sure your token is granted the required export permissions, or contact your tenant administrator.

### 3. Error: "Validation Error: API request failed with status code 400..."
* **What you see:**
  ```text
  Validation Error: API request failed with status code 400. Response: {"status":400,"type":"STATUS:BAD_REQUEST","message":"Malformed query."...}
  ```
* **What it means:** The text you typed in your `-q` search query has a typo or is formatted in a way the search engine does not understand.
* **How to fix it:** Check your query options. Make sure you use standard, clean characters, and match the syntax in our walkthrough examples.

### 4. Error: "Network error occurred... Host is unresolved..."
* **What you see:**
  ```text
  Error: Network error occurred during Circularo API call: Connect to incorrect-domain.com:443 failed: Host is unresolved
  ```
* **What it means:** The app cannot reach the Circularo server. Either your internet connection is down, or your `CIRCULARO_API_URL` value inside `.env` is incorrect.
* **How to fix it:** Open your `.env` file and verify that the `CIRCULARO_API_URL` address is correct, has no typos, and starts with `https://`.
