# Java Assignment 5 – CO5: Data Persistence Engineering
Student: __________ | Register No.: __________ | Java version: __________
Repository name: `CSA09-<RegisterNumber>-Assignment5`

This package contains Part A design/pseudocode, Part B Java source, MySQL schema, sample input, measurement sheets and screenshot folders.

## Run
For non-database Java files, open the folder in VS Code and run:
`javac FileName.java` then `java FileName`.

For JDBC tasks:
1. Run `TaskB3/schema.sql` in MySQL Workbench.
2. Add MySQL Connector/J to the Java classpath.
3. Set environment variables in PowerShell:
`$env:DB_URL="jdbc:mysql://localhost:3306/buspass_db"`
`$env:DB_USER="root"`
`$env:DB_PASSWORD="your_password"`
4. Compile/run with Connector/J:
`javac -cp ".;C:\path\mysql-connector-j.jar" PassLoaderDemo.java PassLoader.java`
`java -cp ".;C:\path\mysql-connector-j.jar" PassLoaderDemo 1000`

## Important submission steps
- Capture your own genuine screenshots after running each program. The screenshot folders are empty placeholders, not fabricated evidence.
- Fill measurement CSVs with your actual timings. Do not copy made-up timings.
- Include screenshots of code, terminal output and MySQL Workbench query results.
- Commit progressively (one commit per task), and share the repo with faculty.
