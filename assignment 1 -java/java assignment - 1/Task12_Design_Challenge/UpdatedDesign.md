# Task 12 – Updated Design, Evaluation, Decision and Defence

## B. Evaluation of the original design (Task 11)

| Question | Finding |
|---|---|
| Can new vehicle types be added **only by writing new subclasses**? | Yes for behaviour: a category is one new `Vehicle` subclass plus one `register(name, constructor)` line. Nothing else has to be edited. (A type that differs only in the *rate* needs just a 4-line subclass.) |
| Are the pricing rules spread across many classes or kept in one place? | **Partly spread, by design.** The base rule (started hours, daily maximum) is in `Vehicle`; each category's *rate or special rule* is in **its own class** (`Car` 30, `TwoWheeler` 10, `EBike` flat, `DeliveryVan` surcharge); waivers are in the `Pass` classes. Each rule is therefore easy to find and change in isolation, but there is **no single table** listing all prices – to see every price one has to read the subclasses. This is a recognised trade-off (see below). |
| Can the existing, tested classes remain unchanged? | **Yes** – verified: 17 of 17 Task 11 files are byte-identical in Task 12 (`./check_unchanged.sh`), and the unchanged Task 11 test suite still passes on the new code (38/38, `output/regression_task11_tests_on_task12_code.txt`). |

## C. Decision: RETAIN and EXTEND (no modification, no redesign)

**Justification:** every changed requirement could be met by *adding* classes that plug into existing abstractions (`Vehicle`, `Pass`) and existing registries (`VehicleFactory`, `CommandProcessor`), and by giving a larger array to a facility that was already jagged. Redesigning would add risk to already tested code without removing any problem. This follows the open/closed principle: open for extension, closed for modification.

### Revised class structure

```
Vehicle (abstract)                                   Pass (interface)
 ├── TwoWheeler                                       ├── MonthlyPass        (Task 11)
 └── Car ──┬── ElectricCar  implements Chargeable     └── DayPass            (NEW)
         └── DeliveryVan                  (NEW)
 └── EBike                                (NEW)

ParkingFacility(int[] slotsPerFloor)  -- unchanged, now {20,30,40,50,60}
VehicleFactory   -- unchanged, +register("EBIKE"), +register("VAN")
CommandProcessor -- unchanged, +register("PASS_DAY")
ParkingApp12 (NEW) -- wires the extended system
```

### What changed

| New file | Lines | Role |
|---|---|---|
| `model/EBike.java` | 22 | Overrides `calculateFee(long)` → flat Rs 20 for every stay (0 minutes included) |
| `model/DeliveryVan.java` | 45 | `extends Car`; overrides `calculateFee(entry, exit)` |
| `model/DayPass.java` | 34 | `implements Pass`; covers a stay that starts and ends on the pass date; price Rs 100 |
| `app/ParkingApp12.java` | 55 | Registers `EBIKE`, `VAN`, `PASS_DAY`; five floors `{20,30,40,50,60}` |
| `app/TestRunner12.java` | 137 | New tests (33) |

Production code added: **156 lines** (EBike + DeliveryVan + DayPass + ParkingApp12). Existing production classes edited: **0**.

### The delivery-van price – correct overlap, not the entry time

Rule implemented: **fee = normal car fee for the whole stay + surcharge for the minutes that overlap 09:00–17:00**, where
`surcharge = ceil(overlapMinutes × carRate / 60)` (i.e. an extra 1× the car rate for those minutes, so they cost **2×** in total). `peakMinutes` intersects the stay with the 09:00–17:00 window of **every calendar day the stay touches**:

```
from = later(entry, windowStart)   to = earlier(exit, windowEnd)   overlap = max(0, to - from)
```

A check on the entry time alone would be wrong in both directions, and the tests catch both:

| Stay | Entry-time-only rule | Correct overlap rule | Test |
|---|---|---|---|
| 08:00–10:00 (enters before 9) | no surcharge → Rs 60 | 60 min overlap → 60 + 30 = **Rs 90** | V3 |
| 16:00–18:00 (enters before 5, leaves after) | full surcharge for 2 h | 60 min overlap → 60 + 30 = **Rs 90** | V4 |
| 17:00–18:00 | – | no overlap → **Rs 30** | V5 |
| 09:00–17:00 | – | exactly double the car cap = **Rs 480** | V7 |
| 23 Nov 16:00 – 24 Nov 10:00 | – | two windows (60 + 60 min) → 300 | V10 |

**Interpretation choices (stated so they can be defended or changed):** (a) the "double rate" is applied as an *added* surcharge on the minutes inside the window, pro-rated per minute and rounded **up** to a whole rupee; the normal part of the stay keeps the car's started-hour rule and daily maximum. (b) The daily maximum applies to the normal part only, so a van that parks for the entire 09:00–17:00 window pays up to 2 × 240 = 480. (c) The e-bike flat fee is Rs 20 per visit and the day pass costs Rs 100 – the assignment gives no amounts; these are assumptions that are easy to change (`EBike.FLAT_FEE`, `DayPass.PRICE`).

### Day pass
`DayPass implements Pass`. `ParkingSystem.exit` asks each registered pass `covers(entry, exit)`; a day pass covers a stay only when both dates equal the pass date, so a stay crossing midnight is charged (test D3). The sale price (Rs 100) is added to `passSalesCollected` and appears in the report, separate from parking fees.

### Five floors
`ParkingApp12.FLOORS = {20,30,40,50,60}` (200 slots). Tests F1–F5 use the capacities `{1,2,3,4,5}` to check the boundaries between floors.

## D. Evidence-based defence of the decision

| Evidence | Result |
|---|---|
| **Existing classes changed** | **0 of 17** (byte comparison); 4 new production files (156 lines) |
| **Ease of adding a further vehicle type** | Example, a *Bus* at Rs 100/hour: write `class Bus extends Vehicle { protected double ratePerHour() { return 100; } }` and add the line `factory.register("BUS", Bus::new);` – **1 new class (~4 lines) + 1 line**, no existing class edited. Adding a new *pass* type is the same: one class implementing `Pass`. |
| **Tests for the new categories** | `TestRunner12`: **33 of 33 passed** (e-bike E1–E4, van V1–V13, day pass D1–D6, floors F1–F5, regression R1–R4, scenario S1) |
| **Regression** | Unchanged Task 11 suite on the Task 12 code: **38 of 38 passed** |
| **Scenario by hand** | Parking: 0 (day pass) + 20 (e-bike) + 90 + 120 + 38 (vans) + 60 (car) + 30 (wrong-date pass, pays) = **358**; pass sales 2 × 100 = **200**; total **Rs 558**. Program: `TOTAL Rs 558.00` ✔ (`output/scenario12_output.txt`) |

### Trade-offs (honest assessment)

1. **Pricing is distributed, not centralised.** Each category owns its rule: very easy to extend, but harder to audit all prices at once. A `PricingRule` strategy (one table or one class family selected per category) would centralise it, at the cost of an extra abstraction. For two special rules the extra layer is not yet justified; if pricing keeps growing (seasonal rates, discounts) introducing a `PricingRule` interface would become the better design – and it could still be added without touching the vehicle classes by injecting a rule into `ParkingSystem`.
2. **`DeliveryVan extends Car`** gives the van the car's rate and daily cap for free, but couples them: if the car rate changes, the van changes too. That is the desired behaviour here ("double the *car* rate"), but it would be wrong if vans got an independent tariff.
3. **The extension points had to exist beforehand.** The zero-change result is not luck but the payoff for the registry/interface/jagged-array choices of Task 11; a design without them would have required modifications (see the counterfactual in `ImpactAnalysis.md`).
4. **Floor sizes are passed at start-up**; adding floors while running is not supported (it would need a resizable structure such as `ArrayList<String[]>`).
5. Money is a `double`; with these whole-rupee rules the results are exact, but `BigDecimal` would be needed if paise-level rules (e.g. ×1.5) appear.

**Final decision:** retain the original design and extend it. The numbers above (0 edits, 156 new lines, 33 + 38 passing tests, hand-calculated total equal to program output) follow directly from the analysis in A and B.
