# Task 2 – Hospital Outpatient Appointment Desk (8 Marks)

## 2.1 Event model
| Event source | Event object | Listener | Action |
|---|---|---|---|
| Department JComboBox | ActionEvent | ActionListener | Refresh doctor choices |
| Book button | ActionEvent | ActionListener | Validate and book |
| Window close control | WindowEvent | WindowAdapter | Ask before closing if booking is active |
| Time-slot JComboBox | ActionEvent | ActionListener (optional) | Update slot availability |
| Patient ID field | ActionEvent | ActionListener (optional) | Move focus/validate field |

## 2.2 Component containment tree
```text
AppointmentDesk (JFrame, BorderLayout)
└── form JPanel (GridLayout 5 x 2)
    ├── JLabel + JTextField (patient ID)
    ├── JLabel + JComboBox<String> (department)
    ├── JLabel + JComboBox<Doctor> (doctor)
    ├── JLabel + JComboBox<String> (time slot)
    └── JLabel + JButton (book)
```

## 2.3 Handler logic
The complete pseudocode is in `Pseudocode.txt`. The implementation loads a small doctor directory into `Map<String,List<Doctor>>`. Changing the department clears and repopulates the doctor combo box. Booking validates the patient ID and selected values, then rejects a doctor/slot combination already booked in the running application.

## 2.4 Event sequence and applet comparison
1. `main` calls `SwingUtilities.invokeLater`.
2. Constructor creates components and registers listeners.
3. `refreshDoctors()` loads the initial doctor list.
4. `setVisible(true)` displays the frame.
5. Department selection generates `ActionEvent`; its listener calls `refreshDoctors()`.
6. Book click generates `ActionEvent`; handler validates and invokes the service.
7. Close control generates `WINDOW_CLOSING`; `windowClosing()` asks for confirmation if booking is active.
8. On approval, `dispose()` releases the native window resources.

An applet uses container lifecycle callbacks `init()`, `start()`, `paint()`, `stop()` and `destroy()`. A standalone JFrame does not use the applet lifecycle; it uses construction, visibility, event callbacks and disposal.

## Alternative concept and complexity
Let `d` be departments and `m` total doctor records. Preloading a map takes O(m) time and O(m+d) memory; a department change retrieves the list in expected O(1) map lookup plus O(m_i) to populate the visible combo for that department. Fetching from a database on every change incurs network/query latency per change (query execution plus O(m_i) returned rows) and can avoid holding all doctors in memory, approximately O(m_i) transient data per request. If the same doctors are repeatedly needed, the preloaded map is faster and avoids repeated database round trips; database fetching is preferable for frequently changing large directories.
