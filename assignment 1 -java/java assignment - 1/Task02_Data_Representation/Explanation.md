# Task 2 – Choose the Data Representation

## 1. Data types chosen

| Field | Type | Why |
|---|---|---|
| slot number | `int` | Whole number, used directly as an array index. |
| floor number | `int` | Same reason. |
| vehicle number | `String` | Mixed letters and digits (`TN01AB1111`); validated by a pattern. Never used in arithmetic. |
| entry / exit time | `java.time.LocalDateTime` in the full system; `int` minutes-since-midnight in the small demo | `LocalDateTime` handles dates, midnight crossing and the 9 AM–5 PM window correctly. Minutes-since-midnight (0–1439) is a simple whole-number alternative for single-day demos. |
| duration | `long` (minutes) | Whole minutes; `long` so that very long stays cannot overflow an `int` in later arithmetic such as `minutes * rate`. |
| fee | `double` | Rupees can be fractional (rates and charges may later include paise). For real money `BigDecimal` is safer; `double` is accepted here because all rates in this assignment are whole rupees. |
| rate | `double` (method `ratePerHour()`) | Same as fee. |
| kWh | `int` | Whole units entered at the gate. |
| user type, pass dates | `enum UserType`, `LocalDate` | Fixed set of values / calendar dates. |

## 2. Representing floors and slots

**Choice: a jagged two-dimensional array `String[][] slots`.** `slots[floor][slot]` stores the registration number or `null` (free).

* `slots[f] = new String[slotsPerFloor[f]]` gives every floor *its own* length, so floors with 20, 40, 60, 80 and 100 slots need no special code.
* `slots.length` is the number of floors and `slots[f].length` the slots on floor *f*, so loops and bounds checks are natural (`release` validates with them).
* A rectangular `new String[F][S]` would waste memory on small floors and let invalid slot numbers look valid.

## 3. Type conversion and the integer-division risk

`DataRepresentation.java` shows the problem for a car (Rs 30/hour) parked 130 minutes:

| Expression | Result | Verdict |
|---|---|---|
| `(minutes / 60) * rate` | **60.0** | WRONG – `130/60` is integer division = 2, losing the started third hour |
| `(int)(minutes / 60.0) * rate` | **60.0** | WRONG – the cast truncates 2.17 to 2 |
| `minutes / 60.0 * rate` | 65.0 | Correct arithmetic, but not the billing rule (started hours are charged in full) |
| `((minutes + 59) / 60) * rate` | **90.0** | Correct – integer ceiling of the hours, then multiplied by the rate |

Other casting risks shown: `(int) 89.5` gives `89` (the fraction is silently lost), while `Math.round(89.5)` gives `90` (an explicit rounding rule).

**How the design avoids the error:** the hour count is computed once, with integer arithmetic only, in `Vehicle.calculateFee`: `billableHours = (remainder + 59) / 60`. The result is promoted to `double` only at the final multiplication by the rate, so no fraction is ever truncated. Minutes are `long` to avoid overflow.

## 4. Reading input

Two methods are supported by `ParkingApp`:

* **File** – `java parking.app.ParkingApp input/scenario.txt` (repeatable, used for the test evidence and screenshots).
* **Scanner on standard input** – run without arguments and type commands.

Command-line arguments carry only the file name. Text commands are used (instead of many positional arguments) because a gate operator sends a *sequence* of events, and a file of commands can be replayed for testing.

## 5. Output of `DataRepresentation.java`

See `Output.txt`. The measured timing line at the end varies with the machine.

---

# Alternative Concept and Complexity Analysis

Alternatives to the jagged array `String[floor][slot]`:

**A. One-dimensional array** with `index = floor × slotsPerFloor + slot`.
**B. `ArrayList<String[]>`**, one `String[]` per floor.

| | Jagged `String[][]` | 1-D flat array | `ArrayList<String[]>` |
|---|---|---|---|
| Floors with **equal** capacity | trivial | `floor*S + slot` works | works |
| Floors with **different** capacity | works unchanged | formula **breaks** – needs a prefix-offset table `floorStart[f]` | works unchanged |
| Locate a slot | O(1): two array accesses | O(1) with the offset table (O(F) to convert an index back into a floor) | O(1): `list.get(f)[s]` (one extra method call) |
| Add a floor later | new outer array (copy O(F)) | rebuild the whole array and the offsets, O(total slots) | `list.add(...)`, amortised O(1) |
| Space | S_total references + F row headers | S_total references + (F+1) offsets | S_total references + F row headers + list overhead |

For the five-floor layout `{20,40,60,80,100}` the naive formula `2*20 + 59 = 99` maps "floor 2, slot 59" to *floor 2, slot 39* (wrong slot, no error raised); the offset table gives the correct index 119 (shown in `Output.txt`).

**Measured** (20 million look-ups, `Output.txt`): jagged = 87 ms, flat + offsets = 63 ms, ArrayList = 72 ms. These are within the same order of magnitude; differences of this size are within JIT/warm-up noise, so no representation is meaningfully faster. The jagged array wins on **simplicity and safety**, which is why it was chosen. Locating a slot is **O(1) time** in all three; space is **O(total slots)** in all three.
