# Task 11 – Design Evaluation and Defence

## Structure of the implementation

```
parking.model    UserType, Vehicle*, TwoWheeler, Car, ElectricCar, Chargeable, Pass, MonthlyPass, ParkingTicket, Receipt
parking.service  ParkingFacility, VehicleFactory, ParkingSystem
parking.app      CommandProcessor, ParkingApp, TestRunner, TicketStoreBenchmark
(* abstract)
```
877 lines of Java in total. 38 automated tests, all passing (`output/test_results.txt`).

## How well the design follows the four principles – with evidence from my own testing

| Principle | How it is applied | Evidence |
|---|---|---|
| **Encapsulation** | All fields `private`; ticket and receipt are immutable; the registration number is validated and normalised in the constructor and has no setter; slot array hidden inside `ParkingFacility`. | Invalid input (I1–I14) is rejected at the boundary of the classes; the facility state never changed after a rejected request (scenario: `STATUS` shows 145/150 after three rejected entries – exactly the five valid vehicles). |
| **Inheritance** | `Vehicle → TwoWheeler / Car → ElectricCar`; common identity and fee rule written once. | `ElectricCar` reuses all of `Car` and adds only `chargingFee`. N3 (EV = 90 + 120) passes. |
| **Polymorphism** | `ParkingSystem` stores `Vehicle` references and calls `vehicle.calculateFee(entry, exit)`; the right rule runs at run time. | There is **no category-testing `if/switch` in `ParkingSystem`** (the single `instanceof Chargeable` tests a *capability*, not a category). Tests N1–N3, B8 give different fees through the same call. |
| **Abstraction** | `Vehicle` (abstract) fixes *what* a vehicle must provide; `Pass` and `Chargeable` are interfaces for replaceable rules and capabilities. | `MonthlyPass` is used by `ParkingSystem` only through `Pass`; the same `registerPass` accepts a different kind of pass in Task 12 without any change. |

## Design decisions that were tested

* **Jagged array** for floors: bounds are checked with `slots[f].length`; I5–I7 prove invalid positions are refused.
* **Registry in `VehicleFactory`** and **command table in `CommandProcessor`**: chosen so that new categories/commands can be added without editing these classes (used in Task 12).
* **Integer ceiling for hours** `(minutes + 59) / 60` – boundary tests B2/B3 (60 → 30, 61 → 60) and the scenario's 181 minutes → 4 hours confirm that no started hour is lost.
* **Duplicate prevention by a `HashMap` keyed by registration number** – I2 passes, including a different letter case.

## Recommendation

Retain this design. The evidence: 38/38 tests including all boundary and invalid cases; the hand-calculated daily collection (Rs 560) equals the program output; no failure was found in testing. (Task 12 then provides a stronger test of the design: it was extended with 0 edits to existing classes.)

## One limitation (and what I would do about it)

**First-fit allocation scans floors and slots linearly (O(F·S) per entry) and treats every slot as the same.** There are no dedicated EV-charging slots, no reserved bays, and a vehicle is not steered to a floor by category. For the sizes in this assignment (≤ 500 slots; 5 × 100 scan measured in Task 3) it is fast enough, but a real campus with thousands of slots would need per-floor free counts / free-slot stacks (Task 3 alternative) and slot types. Other smaller limitations: `double` is used for money (accepted because all rates are whole rupees; `BigDecimal` would be correct for paise); the ticket counter is static and not thread-safe; times are truncated to whole minutes; the application is single-threaded and keeps all data in memory (it is lost when the program ends).

---

# Alternative Concept and Complexity Analysis

**Note:** the assignment text says the active tickets are "currently stored in an array". My implementation stores them in a `HashMap<String, ParkingTicket>` keyed by registration number (chosen deliberately because duplicate detection and exit both need *find by registration number*). The three structures were compared in `TicketStoreBenchmark`.

**Exit processing in each structure**

```java
// array / ArrayList: scan for the ticket, then remove it
for (int i = 0; i < n; i++) if (tickets[i].getVehicle().getRegNo().equals(key)) { found = tickets[i]; tickets[i] = tickets[--n]; break; }
// HashMap: direct hit
ParkingTicket found = activeTickets.remove(key);
```

| | Array `ParkingTicket[]` | `ArrayList<ParkingTicket>` | `HashMap<String, ParkingTicket>` |
|---|---|---|---|
| Find ticket on exit | **O(n)** | **O(n)** | **O(1)** average |
| Remove after exit | O(1) by swapping with the last element (order lost), O(n) if order is kept | **O(n)** (elements shift) | O(1) average |
| Duplicate check on entry | O(n) scan | O(n) scan | **O(1)** `containsKey` |
| Capacity | fixed (must be sized or copied) | grows automatically | grows automatically |
| Space for n parked vehicles | n references | n references + spare capacity (up to ~50 % extra) | n entries + table: roughly **3×–4× the references**, about 48 bytes/vehicle (estimate, not measured) – O(n) |

**Measured** (`output/TicketStoreBenchmark_output.txt`; finding the last of n vehicles, 200 lookups):

| n | array scan | ArrayList scan | HashMap get |
|---|---|---|---|
| 1,000 | 26.25 ms | 3.07 ms | 0.052 ms |
| 10,000 | 40.45 ms | 113.60 ms | 0.069 ms |
| 100,000 | 362.08 ms | 500.05 ms | 0.057 ms |

**Interpretation:** the scans grow roughly with n (10 → 100 times the vehicles gives about 9× → 3× the time for the array, noisy because of JIT warm-up – the n = 1,000 array figure of 26 ms is a warm-up artefact larger than the ArrayList's 3 ms), while the `HashMap` stays at about 0.05–0.07 ms **regardless of n**, matching O(1). For one campus (hundreds of vehicles) any structure is fast enough; the HashMap is chosen because it gives O(1) duplicate prevention and exit with simple code, at the price of more memory per vehicle. Figures vary with the machine and run; the *shape* (growing vs flat) is what matters.
