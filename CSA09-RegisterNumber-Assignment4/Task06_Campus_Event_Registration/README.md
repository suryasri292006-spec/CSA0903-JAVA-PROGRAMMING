# Task 6 – Setup

1. Install MySQL Server and start it.
2. Run `database.sql` in MySQL Workbench.
3. Download MySQL Connector/J and place its JAR in `lib/`.
4. Set `DB_URL`, `DB_USER`, `DB_PASSWORD`.
5. Compile:
   - Windows: `javac -cp ".;lib/mysql-connector-j.jar" *.java`
   - macOS/Linux: `javac -cp ".:lib/mysql-connector-j.jar" *.java`
6. Run:
   - Windows: `java -cp ".;lib/mysql-connector-j.jar" Main`
   - macOS/Linux: `java -cp ".:lib/mysql-connector-j.jar" Main`

Use your actual JAR filename. Never upload credentials to GitHub. The events table is seeded by the SQL script.
