# Programming in Java – Assignment 4 (CO4)
**SIMATS Engineering | CSA09 | Event-Driven GUI Design | 50 Marks**

- Student: Surya Sri
- Register number: `[ENTER REGISTER NUMBER]`
- Java: JDK 17 or later (Swing/AWT); Task 6 requires MySQL Connector/J 8.x or later.

## Contents
Task 1: GUI design review and fee payment separation  
Task 2: Hospital outpatient appointment desk  
Task 3: Campus library search layout  
Task 4: Event-handling forensics and persistent drawing  
Task 5: Four currency-converter event-handling styles  
Task 6: Layered campus event registration application with MySQL JDBC

## Requirements
- JDK 17+
- VS Code with Extension Pack for Java (or a terminal)
- MySQL Server 8+
- MySQL Connector/J JAR for Task 6

## Compile and run (Windows PowerShell)
Open a terminal in the required task folder and run:
```powershell
javac *.java
java FeeWindow
```
Replace `FeeWindow` with the relevant main class. Task 5 classes are run individually.

For Task 6, place the MySQL Connector/J JAR in `Task06_Campus_Event_Registration/lib/`, create the database using `database.sql`, then configure the connection environment variables:
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/campus_events"
$env:DB_USER="root"
$env:DB_PASSWORD="your_password"
javac -cp ".;lib/mysql-connector-j.jar" *.java
java -cp ".;lib/mysql-connector-j.jar" Main
```
Use the actual Connector/J JAR filename in place of `mysql-connector-j.jar`. Do not commit database passwords.

## Task 1–5
Each folder contains source code, design notes and pseudocode. These programs use sample/in-memory data where a database is not required by the task.

## Task 6 database setup
1. Start MySQL.
2. Open `Task06_Campus_Event_Registration/database.sql` in MySQL Workbench.
3. Execute the script.
4. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD`.
5. Compile and run using the classpath command above.

## Evidence
The `Screenshots/` folders contain `SCREENSHOT_INSTRUCTIONS.md` files only. They are not fabricated screenshots. Run each program, test the described interaction and save genuine screenshots in the matching folder.

## Testing status
Source and design artifacts are supplied. Actual runtime results must be recorded by the student after executing on their computer and connecting their own MySQL server. Do not mark a test PASS until it has been run.

## GitHub
Create repository `CSA09-[RegisterNumber]-Assignment4`. Extract this ZIP, open the extracted folder in VS Code, initialize Git, commit each task separately, and push the files (not the ZIP archive). Add the faculty as collaborator if the repository is private.

```bash
git init
git add README.md Report
git commit -m "Add assignment report and project overview"
git add Task01_Gui_Design_Review
git commit -m "Complete Task 1 GUI design review"
# Repeat git add / git commit for Tasks 2–6 and evidence.
git branch -M main
git remote add origin https://github.com/USERNAME/CSA09-REGISTERNO-Assignment4.git
git push -u origin main
```

## Academic integrity and verification
Review every explanation, run every program, capture genuine evidence, replace personal placeholders, and ensure you can explain the handlers, listeners, JDBC flow and complexity analysis during viva.
