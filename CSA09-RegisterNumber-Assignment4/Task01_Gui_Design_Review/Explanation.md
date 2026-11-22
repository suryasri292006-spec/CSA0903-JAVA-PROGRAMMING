# Task 1 – GUI Design Review (6 Marks)

## 1.1 Diagnosis (2 marks)
| Deficiency | User/system effect |
|---|---|
| UI, validation, JDBC and payment are in one handler | Difficult to test and maintain; a database change affects the window code. |
| `==` compares String references | The Pay branch may not run reliably; use `"Pay".equals(e.getActionCommand())` or separate listeners. |
| No empty/numeric/range validation | Blank or malformed amounts cause exceptions; zero/negative amounts may be recorded. |
| JDBC and `Thread.sleep(3000)` run on the event-dispatch thread | The window freezes and cannot repaint/respond while work is running. |
| SQL is built by concatenating user input | SQL injection and quoting errors are possible. |
| No clear success/error feedback | User cannot tell whether payment succeeded. |
| Receipt button is not implemented | User cannot view or verify a receipt. |
| Clear is treated as every non-Pay action | Receipt accidentally clears fields instead of showing a receipt. |
| No window lifecycle/connection cleanup | Resources may remain open. |
| Checked SQL exceptions are not handled | Program may not compile or may fail without useful feedback. |

## 1.2 Repaired design (3 marks)
`FeeWindow` collects values, wires separate button listeners and displays results. `FeeService` validates the values and records the payment using `PreparedStatement`. The supplied example reads connection settings from environment variables. The `fees` table must exist with `register_number` and `amount` columns. For a production application, use a connection pool and execute JDBC work in `SwingWorker` so the event-dispatch thread is not blocked. The example closes statements automatically; the connection should be closed when the window is disposed in a production refinement.

Class relationship:
```text
FeeWindow (JFrame)
  |-- Pay / Clear / Receipt listeners
  |-- input fields and status
  `-- calls FeeService
         `-- JDBC Connection -> PreparedStatement -> fees table
```

## 1.3 Design defence (within 100 words; 1 mark)
The redesigned window has one responsibility: collecting input and presenting feedback. FeeService owns validation and payment persistence, so it can be tested without opening a GUI. PreparedStatement protects SQL parameters, and separate listeners make each button's purpose clear. Changes to payment rules do not require rewriting the layout. This separation reduces duplication, improves readability and makes defects easier to isolate.

## Alternative listener objects and complexity
With one shared listener and an `if-else` chain, dispatch may compare up to `k` action commands: worst-case O(k) comparisons and O(1) auxiliary space. With a dedicated listener/action per button, the event is routed directly to the registered handler: conceptually O(1) handler selection, with O(k) listener objects/references in total. Actual GUI dispatch overhead is framework-dependent; Big-O describes the application-level selection logic. Dedicated handlers use more objects but avoid a growing conditional chain.
