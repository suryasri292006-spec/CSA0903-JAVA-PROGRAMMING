# Task 3 – Control Logic (discussion, Complete the Code, alternative analysis)

## Discussion

* **How the search works:** two nested `for` loops – floors in order, slots in order. The first slot whose value is `null` is taken (first-fit), the vehicle number is stored there and `{floor, slot}` is returned. Because of the early `return`, the search stops at the first free slot; if none exists every slot is visited and `null` is returned.
* **When no suitable slot is free:** `allocate` returns `null`. `ParkingSystem.enter` turns that into an `IllegalStateException("Parking is full")`; no ticket is created and no state changes.
* **Fee slabs:** expressed with `switch` (rate by vehicle type) and `if–else` (first-hour slab, additional hours, daily maximum) in `FeeCalculator.java`; the integrated system uses the same rule in `Vehicle.calculateFee`.
* **`break`, `continue`, `return`:** `continue` – skip an occupied slot; `return` – leave both loops at the first free slot and reject invalid input early; `break` is used in the ticket search of `TicketStoreBenchmark` once the key is found.

## Complete the Code – `ParkingFacility.java`

Only the two `TODO` bodies were replaced; the constructor and `main()` are unchanged.

* **TODO 1 `allocate`** – the nested loops described above.
* **TODO 2 `release`** – three guards (floor range, slot range, already free) each returning `false`, then clearing the slot and returning `true`.

Actual output (`Output.txt`) – identical to the expected output:

```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```

## Fee computation demo

`FeeCalculator.java` prints a table of fees for 0, 30, 60, 61, 130, 480, 481, 1440 and 1441 minutes (`FeeCalculator_Output.txt`), covering the zero-minute case, the slab boundary (60 → 61 minutes), the daily-maximum boundary (480 → 481) and the day boundary (1440 → 1441), plus an unknown type and a negative duration.

---

# Alternative Concept and Complexity Analysis

**Alternative:** keep a **free-slot stack per floor** and a **free-count per floor** (`AllocationBenchmark.StackFacility`).

```
allocate: for each floor f in order: if freeCount[f] > 0 → s ← freeStack[f].pop(); slots[f][s] ← vehicleNo; freeCount[f]--; return {f,s}
release : validate; slots[f][s] ← null; freeStack[f].push(s); freeCount[f]++
```

Let F = number of floors and S = slots per floor.

| | Scan (given code) | Stack + counts |
|---|---|---|
| `allocate` time, worst case | **O(F·S)** – a nearly full facility is scanned almost completely | **O(F)** – only the counts are scanned, the slot comes from an O(1) `pop` (O(1) if a global "first floor with space" pointer is kept) |
| `release` time | **O(1)** | **O(1)** (`push`) |
| Extra space | **O(1)** besides the `slots` array | **O(F·S)** for the stacks + **O(F)** for the counts |
| Code complexity | smallest | must keep three structures consistent |
| Allocates the lowest free slot? | yes, always | only if the stack is initialised so that the lowest number is on top **and** released slots are pushed back – after releases a *higher* slot can be on top, so the lowest-slot guarantee is lost |

**Measured** (`AllocationBenchmark_Output.txt`, worst case, 200,000 allocate+release pairs):

| Facility | slots | scan | stack + counts |
|---|---|---|---|
| 1 × 20 | 20 | 25 ms | 33 ms |
| 3 × 50 | 150 | 36 ms | 13 ms |
| 5 × 100 | 500 | 62 ms | 16 ms |

On the small facility the scan is as fast (or faster) – the stack has overhead. As the facility grows the scan grows with F·S while the stack version stays almost flat, matching O(F·S) versus O(F). For the real sizes in this assignment (≤ 500 slots) the plain scan is already fast enough, so the simpler scan is the better engineering choice here.
