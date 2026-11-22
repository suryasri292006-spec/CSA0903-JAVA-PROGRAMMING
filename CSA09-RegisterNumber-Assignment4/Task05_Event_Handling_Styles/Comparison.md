# Task 5 – Same Screen, Different Event-Handling Styles (7 Marks)

Four independent runnable classes are provided. Each uses the same illustrative fixed rates (1 INR = 1 INR, 1 USD = 83 INR, 1 EUR = 90 INR); these are teaching constants, not live exchange rates. Each implementation supports the Convert button and Enter in the amount field, shows a dialog, validates input and uses `WindowAdapter`.

| Style | Readability | Access to frame fields | Reuse | Change effort |
|---|---|---|---|---|
| Frame implements ActionListener | Simple for a tiny frame; mixes responsibilities | Direct | One frame handler can serve controls | More changes if frame already has another listener role |
| Named inner class | Clear named handler; good for larger logic | Direct access to enclosing instance fields | High; handler can be attached to multiple sources | Usually easiest to extend/test among these four |
| Anonymous inner class | Handler stays near registration point | Direct | Limited unless stored in a variable | Local changes are convenient; verbose |
| Lambda | Concise for one-method interface | Lexically accesses enclosing fields | Reuse possible by storing ActionListener variable | Very small change for another trigger |

### Task 5.2 – requirement change
The shared handler is registered on both the button and amount field in all versions. Showing the result in a dialog is done in the shared `convert()` method. The change is therefore one additional listener registration plus the dialog display code; the core conversion logic is not duplicated.

### Method reference and shared Action
A method reference `this::convert` can be used if `convert` has a compatible signature (for ActionListener, use `e -> convert()` unless an overload accepts ActionEvent). A shared `Action` can provide common text, enabled state and action behavior to a button, menu item and keyboard binding. It centralizes behavior and makes adding a trigger mostly a registration step.

### Complexity
Currency conversion performs a constant number of parsing, lookup and arithmetic operations: O(1) time and O(1) auxiliary space per event. Each approach creates a constant number of handler objects for this small screen. Across k triggers, separately created listeners may use O(k) listener objects; one shared handler/Action can use O(1) handler objects plus O(k) registrations. GUI framework dispatch costs are not meaningfully distinguished by these asymptotic bounds.
