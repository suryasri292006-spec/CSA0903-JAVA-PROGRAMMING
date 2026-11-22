# Task 3 – Campus Library Search Screen (9 Marks)

## 3.1 Layout managers
- Main frame: `BorderLayout` – title/search at NORTH, results at CENTER, controls/status at SOUTH. CENTER receives extra space when resized.
- Top nested panel: `BorderLayout` – title in NORTH and search row in SOUTH.
- Search row: `FlowLayout(LEFT)` – keeps field, category and button together at their preferred sizes.
- Bottom panel: `BorderLayout` – navigation row in CENTER and status in SOUTH.
- Navigation row: `FlowLayout(CENTER)` – buttons retain preferred sizes.

## 3.2 Nested container tree
```text
JFrame (BorderLayout)
├── top JPanel (BorderLayout) [NORTH]
│   ├── title JLabel [NORTH]
│   └── searchRow JPanel (FlowLayout) [SOUTH]
├── JScrollPane(JTable) [CENTER – grows]
└── bottom JPanel (BorderLayout) [SOUTH]
    ├── buttonRow JPanel (FlowLayout) [CENTER]
    └── status JLabel [SOUTH]
```

## 3.3 Implementation
`LibrarySearch.java` is a working Swing implementation with sample catalogue data, category/title filtering, five-row paging, previous/next controls and reservation feedback. Reservation is a demonstration action, not a live library backend.

## 3.4 Resize tests
Run the program and capture three genuine screenshots:
1. 600 × 400 – verify controls remain visible and the table is usable.
2. 800 × 500 – verify the results area receives additional width and height.
3. 1100 × 700 – verify the centre table expands while top/bottom controls remain at preferred sizes.

Record the actual observation after execution. Do not use generated mock screenshots.

## Alternative concept and complexity
Nested BorderLayout/FlowLayout uses O(c) layout work for c components in the containers and adds a few small panel objects. GridBagLayout can arrange more complex forms flexibly but requires constraints per component; layout work remains proportional to component count in typical use, with more configuration. Absolute positioning (`setLayout(null)`) has little layout-manager computation but requires manually recalculating every bound on resize; without resize logic, controls overlap or remain stranded. It is therefore the least responsive option. Container space is O(p) for p nested panels in the nested design; a GridBag design may use fewer panels, while absolute positioning can also use fewer containers but transfers complexity to manual code.
