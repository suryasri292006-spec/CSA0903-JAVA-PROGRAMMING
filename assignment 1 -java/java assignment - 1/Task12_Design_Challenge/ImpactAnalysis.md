# Task 12-A – Impact Analysis

## What has changed from the original problem

| # | Change | Kind of change |
|---|---|---|
| 1 | **E-bike** – flat parking fee | New vehicle category, new pricing rule (fee independent of time) |
| 2 | **Delivery van** – double the car rate during 9 AM–5 PM | New vehicle category, new **time-of-day** pricing rule |
| 3 | **One-day pass** for visitors | New kind of pass (the original only had a monthly pass for students/staff); also *sold* for money |
| 4 | **Facility grows from 3 to 5 floors with different slot counts** | Larger, **non-uniform** layout |

## Which classes, methods and data structures are affected?

The answer depends on how the original (Task 11) was built, so both the actual result and the counterfactual are given.

| Area | Affected? | Actual outcome in my design |
|---|---|---|
| `Vehicle` hierarchy | New categories needed | **Extended**: `EBike`, `DeliveryVan` are new subclasses; `Vehicle`, `Car`, `TwoWheeler`, `ElectricCar` untouched |
| `Vehicle.calculateFee` | Van needs the clock, e-bike ignores it | Already had two overloads, `calculateFee(long minutes)` and `calculateFee(entry, exit)`. `EBike` overrides the first, `DeliveryVan` overrides the second. |
| `VehicleFactory` (category name → object) | Must know "EBIKE" and "VAN" | **Not edited** – it is a registry; the two names are registered by the new `ParkingApp12`. |
| `Pass` / `ParkingSystem.registerPass` | Day pass must waive the fee and be sold | **Not edited** – `ParkingSystem` only uses the `Pass` interface (`covers`, `getPrice`); `DayPass` is a new implementation. |
| `CommandProcessor` | New command `PASS_DAY` | **Not edited** – commands are a table; `PASS_DAY` is registered by `ParkingApp12`. |
| `ParkingFacility` (slots array) | Five floors of different size | **Not edited** – it already takes `int[] slotsPerFloor`; `{20,30,40,50,60}` is just a new argument. |
| Collection / report | Pass sales are revenue | Already separated (`passSalesCollected`). |
| Tests | New cases | New `TestRunner12`; Task 11 tests kept as regression. |

## Why a different number of slots on each floor affects the array design

* A **rectangular** array `new String[floors][slotsPerFloor]` has one size for all floors; floors with 20 and 60 slots would force either wasted slots (invalid slot numbers that look valid) or a separate size table.
* A **flat one-dimensional array** with `index = floor × slotsPerFloor + slot` gives **wrong slots without any error** when floors differ (`2 × 20 + 59 = 99` points to slot 39 of floor 2 instead of slot 59 – shown in Task 2). It needs a prefix-offset table.
* A **jagged array** (`slots[f] = new String[size_f]`) has a length per row, so every loop and bounds check (`slots[f].length`) is already correct. This was the representation chosen in Task 2 and used in Task 11, which is why this change needs **no code change** (test F1–F5: floors `{1,2,3,4,5}` fill in the right order; vehicle 16 is refused).

## Which classes would have to be modified rather than extended?

**In my design: none (0 of 17 existing classes were edited – verified by byte comparison, `output/existing_class_change_check.txt`).**

This result is a consequence of four extension points built into Task 11 *before* the new requirements were known: the jagged array, the `VehicleFactory` registry, the `Pass` interface and the `CommandProcessor` command table. The counterfactual shows what they were worth. Had Task 11 used the common shortcuts, then:

| Shortcut in an original design | Class that would have to be **modified** now |
|---|---|
| `switch (type)` for creating vehicles | the factory / `ParkingSystem.enter` |
| `if (userType == STUDENT) free` instead of a `Pass` object | `ParkingSystem.exit` (and every place that checks the pass) |
| A rectangular array or flat index | `ParkingFacility` and every user of its indexes |
| A single `calculateFee(int minutes)` only | `Vehicle` and every caller (the van needs entry **and** exit times) |
| `if/else` command parsing | the command-handling class |
