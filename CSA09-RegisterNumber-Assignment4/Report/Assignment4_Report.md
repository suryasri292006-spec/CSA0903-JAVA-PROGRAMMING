# SIMATS ENGINEERING
## PROGRAMMING IN JAVA (CSA09)
## ASSIGNMENT 4 – CO4: EVENT-DRIVEN GUI DESIGN

**Student Name:** Surya Sri  
**Register Number:** [ENTER REGISTER NUMBER]  
**Java Version:** JDK 17 or later  
**Total Marks:** 50

---

# Objective
To analyse and design event-driven Java interfaces, use AWT/Swing components and listeners, apply layout managers, debug event handling, compare listener implementation styles, and develop a layered Swing–JDBC application.

# Task 1 – GUI Design Review (6 Marks)
The given fee window mixes presentation, validation, SQL and waiting in one event handler. It compares action strings with `==`, does not validate inputs, builds SQL by concatenation, blocks the event-dispatch thread with database work and `sleep`, gives poor feedback, does not implement Receipt correctly, and lacks robust resource/exception handling.

The repaired design separates `FeeWindow` and `FeeService`. The window gathers values and displays status. The service validates register number and amount and inserts using a parameterized `PreparedStatement`. See `Task01_Gui_Design_Review/` for executable source, pseudocode and the design defence.

**Alternative:** a chain of k command comparisons has worst-case O(k) selection; separate listener objects provide direct routing at the cost of O(k) listener storage. Application-level handler selection is approximately O(1) for the dedicated-listener approach.

# Task 2 – Hospital Outpatient Appointment Desk (8 Marks)
The event model maps department and booking controls to ActionEvent/ActionListener and closing to WindowEvent/WindowAdapter. The containment tree uses JFrame, JPanel, JLabel, JTextField, JComboBox and JButton. Department changes refresh doctors from a `Map<String,List<Doctor>>`; booking validates patient ID, department, doctor and slot and rejects an already-booked doctor/slot pair.

The application event sequence is construction, listener registration, initial refresh, visibility, action callbacks and windowClosing/dispose. This differs from the applet lifecycle (`init`, `start`, `paint`, `stop`, `destroy`). Preloaded map data uses O(m+d) memory and avoids repeated database requests; database fetching transfers only the selected department's records but incurs query/network latency on every change.

# Task 3 – Campus Library Search Screen (9 Marks)
The implementation uses nested BorderLayout and FlowLayout panels. The centre JScrollPane/JTable expands; search and navigation controls retain preferred sizes. It supports sample title/category search, five-record pages and reservation feedback.

The layout tree and resizing test plan are in `Task03_Library_Search_Layout/Layout_Design.md`. Capture actual screenshots at 600×400, 800×500 and 1100×700. GridBagLayout offers flexible constraints but requires more configuration. Absolute positioning has little layout-manager work but does not adapt to resizing unless bounds are recalculated manually.

# Task 4 – Event-Handling Forensics (7 Marks)
The callback must be spelled `mouseClicked`, not `mouseclicked`. Without a correctly overridden callback the click is not handled. The original code also fails to update the label and draws directly through `getGraphics()`, so marks disappear on repaint. The repair stores Points in an ArrayList, updates the label, calls repaint and redraws all points in paint. `@Override` makes the misspelling a compile-time error.

The point-list solution uses O(n) memory and O(n) work per repaint. A BufferedImage uses O(width×height) pixel storage and repaints by copying the image; it avoids redrawing each primitive but can consume more memory.

# Task 5 – Event-Handling Styles (7 Marks)
Four separate converters implement frame listener, named inner class, anonymous inner class and lambda styles. Each accepts button click and Enter, validates input, displays a formatted result/dialog and closes through WindowAdapter. Rates are illustrative fixed constants, not live currency rates.

A named inner class is suitable for reusable, substantial handler logic; lambdas are concise for short functional-interface callbacks. A shared Action can serve buttons, menu items and keyboard shortcuts. Conversion itself is O(1) time and O(1) auxiliary space.

# Task 6 – Campus Event Registration (13 Marks)
The layered application has a JFrame UI, service validation, DAO SQL operations, a Registration data model and a connection factory. It provides registration, JTable display, event filtering, pagination, CSV export and a JMenuBar. `SwingWorker` keeps database operations off the event-dispatch thread. `PreparedStatement` binds values and a database UNIQUE constraint on `(register_number,event_id)` prevents duplicates.

For 100,000 rows, loading all rows transfers and stores O(N) data. Pagination transfers/stores O(P) per page, where P is page size, with a separate query cost per page. LIMIT/OFFSET is included as required.

# Test Cases and Results
Use `Test_Cases/Test_Cases.md`. The project package intentionally does not claim unexecuted test outcomes. Run the programs, record the actual output and capture screenshots on the target machine before submission.

# Conclusion
The assignment demonstrates event-driven GUI design, event delegation, listener alternatives, layout managers, repaint-safe drawing, separation of concerns and Swing–JDBC integration. The implementation is organized by task and includes run instructions and evidence locations.

# GitHub Submission
Create `CSA09-[RegisterNumber]-Assignment4`, extract the project folder, upload the individual files/folders (not the ZIP archive), commit progressively by task and share the repository link with faculty.
