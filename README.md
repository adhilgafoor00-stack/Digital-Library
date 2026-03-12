# Professional Digital Library 📚

A modern, web-based digital library management system built with Java. This application features a sleek glassmorphism UI, real-time dashboard analytics, and a hardened SQLite database backend.

## ✨ Features

- **Live Dashboard:** Real-time summary cards for Books, Members, and Issued items.
- **Recent Activity Log:** Live-stream of library transactions with book and member details.
- **Inventory Management:** Full CRUD (Create, Read, Update, Delete) for Books and Members.
- **Hardened Security:** SQL injection prevention using PreparedStatements and robust server-side validation.
- **Modern UI:** Responsive dark-themed interface with glassmorphism aesthetics.
- **Embedded Server:** No external web server needed; carries its own embedded Java HTTP server.

---

## 🚀 Getting Started on Windows (VS Code)

Follow these steps to set up and run the project on a fresh Windows machine.

### 1. Prerequisites
- **Java Development Kit (JDK):** Install [JDK 21 or later](https://www.oracle.com/java/technologies/downloads/).
- **Git:** Install [Git for Windows](https://git-scm.com/download/win) (optional but recommended).
- **Visual Studio Code:** Install [VS Code](https://code.visualstudio.com/).
- **VS Code Extensions:** Install the **"Extension Pack for Java"** from Microsoft.

### 2. Project Setup
If you are starting fresh, open your terminal (Command Prompt or PowerShell) and run:

```powershell
# 1. Clone the repository
git clone https://github.com/adhilgafoor00-stack/Digital-Library.git

# 2. Enter the project directory
cd "digital library"

# 3. Open in VS Code
code .
```
*(If you downloaded the ZIP, simply extract it and open the folder in VS Code.)*

### 3. Compilation
Open the integrated terminal in VS Code (`Ctrl + ` `) and run this exact command to compile all files:
```powershell
javac -cp ".;sqlite-jdbc.jar" *.java
```

### 4. Running the Application
Execute this command to start the server:
```powershell
java -cp ".;sqlite-jdbc.jar" MainApplication
```

### 5. Access the Library
Once the server starts, open your browser and go to:
**[http://localhost:8080](http://localhost:8080)**

**Default Credentials:**
- **Username:** `admin`
- **Password:** `admin123`

---

## 🛠️ VS Code Configuration (Optional)
To run the project using the VS Code "Run" button, add a `.vscode/launch.json` file:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch Library",
            "request": "launch",
            "mainClass": "MainApplication",
            "classPaths": [
                ".",
                "sqlite-jdbc.jar"
            ]
        }
    ]
}
```

---

## 🔒 Security & Performance
- **Resource Management:** Uses Java's `try-with-resources` to prevent database connection leaks.
- **Data Integrity:** Implements `LEFT JOIN` logic for the dashboard to ensure activity logs remain visible even if records are archived.
- **Concurrency:** Uses a multi-handler architecture for efficient request processing.

---
*Developed as a high-performance Java Web Application.*
