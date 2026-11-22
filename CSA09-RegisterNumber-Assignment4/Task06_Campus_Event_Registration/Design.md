# Task 6 – Campus Event Registration Desktop Application (13 Marks)

## Stage 1 – Problem interpretation (2 marks)
**Users:** students (registration form) and committee members (view/filter/export).  
**Screens:** registration and registration-list screen in one JFrame; confirmation/error dialogs.  
**Data:** student name, register number, event ID/name, registration ID and timestamp.  
**Rules:** required values; register number format; event must exist; one student may register only once per event; list can be filtered and exported.

Testable requirements:
1. Blank student name is rejected.
2. Blank/invalid register number is rejected.
3. A valid student/event pair is inserted.
4. Repeating the same register-number/event pair is rejected by a database unique constraint.
5. All records can be displayed in pages.
6. Event filter returns only matching rows.
7. CSV export includes a header and all registrations.
8. Database work is performed in SwingWorker, not on the event-dispatch thread.

## Stage 2 – Screen and event flow (2 marks)
```text
Main -> RegistrationFrame
       |
       +-- load events (SwingWorker) -> event JComboBox
       +-- Register button -> validate -> service -> DAO/JDBC -> MySQL
       |                                  |
       |                                  +-> success/error dialog
       +-- Filter -> SwingWorker -> DAO SELECT -> JTable
       +-- Previous/Next -> paginated SELECT -> JTable
       +-- Export CSV -> SwingWorker -> file chooser -> CSV
       +-- File/Refresh, File/Exit, Help/About menu actions
```
The UI uses a JMenuBar, registration form, filter controls, JTable in JScrollPane and status label.

## Stage 3 – Critical decisions (2 marks)
- GUI captures data; service validates business rules; DAO owns SQL; DatabaseConnection centralizes connection configuration.
- `PreparedStatement` binds all user values.
- The database UNIQUE constraint is the final authority for duplicate prevention, avoiding race conditions that a GUI-only check could miss.
- `SwingWorker.doInBackground()` performs JDBC operations; `done()` updates Swing components on the event-dispatch thread.
- Credentials are read from environment variables and are not hard-coded.

## Stage 4 – Classes and handlers (4 marks)
```text
Main
  `-- RegistrationFrame (JFrame)
        `-- RegistrationService
              `-- RegistrationDAO
                    `-- DatabaseConnection -> MySQL
Registration (data model)
```
**Register pseudocode:** see `Pseudocode.txt`. The handler snapshots form values, disables the Register button, executes the service in SwingWorker, then displays success/error and refreshes the page.

**Filter pseudocode:** read selected event, compute offset = page × pageSize, execute parameterized SELECT with optional event predicate and LIMIT/OFFSET in SwingWorker, then replace JTable rows in `done()`.

## Stage 5 – Implementation and demonstration (3 marks)
The source files implement Swing UI, JDBC DAO, service validation, MySQL schema, unique duplicate constraint, pagination and CSV export. Configure MySQL Connector/J and environment variables as described in the root README. Demonstrate:
- Normal: register a new register number for an event.
- Invalid: submit blank name or malformed register number.
- Duplicate: submit the same register number and event again.
Capture genuine screenshots and record actual outputs.

## Alternative: load all records vs pagination
For N=100,000 records, loading all rows has O(N) transfer, processing time and client memory, plus a large EDT table update can make the UI sluggish (the supplied code uses a background worker, but retaining/rendering all rows still costs memory). Pagination with page size P transfers O(P) rows per page and uses O(P) client row memory; each request pays query/network overhead and OFFSET scans can become more expensive at large offsets. An index on the filtering/order columns and keyset pagination can improve deep-page performance. The supplied rubric explicitly asks LIMIT/OFFSET, which is implemented. Pagination keeps the visible table responsive and limits memory to the current page.
