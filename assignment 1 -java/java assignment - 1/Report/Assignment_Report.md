# Assignment Report – Campus Smart-Parking System

**Assignment:** Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System (CO1, 50 marks)
**Course:** Java Fundamentals and Object-Oriented Programming
**Student name:** [MY NAME]  **Register number:** [MY REGISTER NUMBER]
**GitHub repository link:** https://github.com/<your-username>/CSA09-<RegisterNumber>-Assignment1  *(fill in – required on the cover page)*
**Java version:** 17

> All program outputs in this report were produced by actually running the code. Screenshots are **not** included: insert your own (see `SCREENSHOT_GUIDE.md`). Timing figures in benchmark outputs vary by machine.

## Overall aim and conclusion

Aim: model a multi-floor campus parking system in Java using object-oriented principles, validate it by testing, and re-evaluate the design when requirements change.
Conclusion: the design (abstract `Vehicle` hierarchy, `Pass` and `Chargeable` interfaces, jagged-array facility, registries for categories and commands) passed 38/38 tests in Task 11, and absorbed all Task 12 changes with **0 of 17 existing classes edited**, 33/33 new tests and 38/38 regression tests passing. The full UML diagram is in `UML_Diagram.md`.

---

# Task 1 – Understand the Problem and Identify the Objects

**Aim.** Identify the classes, responsibilities, rules and assumptions of the parking problem.

**Problem analysis.** Inputs: registration number, category, user type, times, pass, kWh, facility layout. Outputs: slot, ticket, fee, availability, errors. Nouns → classes `Vehicle` (+ subclasses), `ParkingTicket`, `ParkingFacility`, `ParkingSystem`, `Pass`, `Receipt`; slot/floor stay as data in a jagged array; user kinds are an enum because they differ only in pass eligibility. 12 business rules (e.g. a slot never holds two vehicles, ticket numbers never reused, no duplicate registration) and the assumptions (rates Rs 10/30 per hour, daily maximum 8 hours) are in `Task01_Problem_Analysis/ProblemAnalysis.md`.

**Algorithm / pseudocode.** Not required for this task.

**Implementation details and Java code references.** `Task01_Problem_Analysis/ProblemAnalysis.md`, `ComplexityAnalysis.md`, `ParallelArrayVsObjects.java`.

**Output evidence.**
```
Worst-case search (key is the LAST vehicle), 200 searches per size
n         parallel arrays    object list        HashMap (extra)   
1000      26.41 ms           35.98 ms           0.490 ms           (sink 599400)
10000     111.89 ms          117.22 ms          0.425 ms           (sink 2543400)
100000    156.67 ms          178.58 ms          10.370 ms          (sink 20255400)

Adding a new attribute (e.g. ownerName):
  parallel arrays : declare a new array, grow it everywhere, change every method that moves/deletes a vehicle
  objects         : add ONE field to Vehicle; existing code keeps working
```

**Test cases.** Search benchmark with n = 1,000 / 10,000 / 100,000.

**Complexity analysis.** Parallel arrays: search O(n), new attribute = edit every method; objects: search O(n) (O(1) with a HashMap), new attribute = one field. Space O(n) in both.

**Result.** Candidate classes, responsibilities, rules and assumptions identified and justified; alternative design compared with measurements.

---

# Task 2 – Choose the Data Representation

**Aim.** Choose data types and an array structure and avoid casting errors.

**Problem analysis.** Slot/floor `int`, vehicle number `String`, duration `long` minutes, fee `double`, time `LocalDateTime`. Floors are a jagged array because floors differ in size. A fee of `(130/60)*30` gives Rs 60 instead of Rs 90 because of integer division.

**Algorithm / pseudocode.** Hours = `(minutes + 59) / 60` (integer ceiling) before multiplying by the rate.

**Implementation details and Java code references.** `Task02_Data_Representation/DataRepresentation.java`, `Explanation.md`.

**Output evidence.**
```
Slot=7, Vehicle=TN01AB1111, Entry=555, Exit=700, Duration=145 min

--- Integer division risk (130 minutes, Rs 30/hour) ---
Wrong   (minutes / 60) * rate        = 60.0
Wrong   (int)(minutes / 60.0) * rate = 60.0
Pro-rata minutes / 60.0 * rate       = 65.0 (not the billing rule)
Correct ((minutes + 59) / 60) * rate = 90.0

(int) 89.5 = 89 , Math.round(89.5) = 90

Total slots = 300
Naive index (floor*S+slot with S=20) = 99  -> floor 2, slot 39 (WRONG slot, expected 59)
Correct index with offsets          = 119  -> floor 2, slot 59 (correct)
ArrayList floors = 5, floor 2 slot 59 = TN01AB1111
Jagged floor 2 slot 59 = TN01AB1111
Capacities = [20, 40, 60, 80, 100]

Time for 20000000 slot look-ups (ms): jagged=87, flat+offsets=63, ArrayList=72  (checksum 0)
```

**Test cases.** Integer division, truncating cast, naive flat index versus offset table.

**Complexity analysis.** Locating a slot is O(1) in jagged, flat+offsets and `ArrayList<String[]>`; space O(total slots) in all. The naive flat formula silently returns the wrong slot for unequal floors.

**Result.** Data types justified; casting risk identified (60.0 vs 90.0) and removed.

---

# Task 3 – Design the Control Logic

**Aim.** Design slot allocation and fee computation; complete `ParkingFacility`.

**Problem analysis.** Search floors and slots in order, stop at the first free slot, return `null` when full; fee slabs with `switch`/`if–else`; `continue` skips occupied slots, `return` ends both loops.

**Algorithm / pseudocode.** Full pseudocode for AllocateSlot, ReleaseSlot and ComputeFee in `Task03_Control_Logic/Pseudocode.md` (note there on the SIMATS standard, which was not supplied with the assignment).

**Implementation details and Java code references.** `ParkingFacility.java` (only TODO bodies written; `main` unchanged), `FeeCalculator.java`, `AllocationBenchmark.java`.

**Output evidence.**
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```
The output equals the expected output of the assignment.

**Test cases.** Allocation to the full facility, release valid / repeated / invalid, re-allocation; fee table for 0, 30, 60, 61, 130, 480, 481, 1440, 1441 minutes.

**Complexity analysis.** Scan: allocate O(F·S), release O(1), extra space O(1). Stack + free counts: allocate O(F), release O(1), extra space O(F·S). Measured (200,000 pairs): 5×100 slots scan 62 ms vs stack 16 ms.

**Result.** Allocation and fee logic correct; expected output reproduced.

---

# Task 4 – Design Classes, Objects and Constructors

**Aim.** Design `Vehicle` and `ParkingTicket` with justified constructors.

**Problem analysis.** Default, overloaded, parameterised and copy constructors; `this`; chaining to one full constructor; copy constructor for duplicate tickets; references on the stack, objects on the heap.

**Algorithm / pseudocode.** Not required.

**Implementation details and Java code references.** `Task04_Classes_Objects_Constructors/Vehicle.java`, `ParkingTicket.java`, `Task04Demo.java`, `Explanation.md`.

**Output evidence.**
```
CAR[UNKNOWN] | CAR[TN01AB1111] | EV[TN02CD2222] | EV[TN02CD2222]
Ticket#1001 EV[TN02CD2222] slot=[0,1] entry=540
Ticket#1002 CAR[TN01AB1111] slot=[-1,-1] entry=555
Ticket#0 CAR[UNKNOWN] slot=[-1,-1] entry=0
Ticket#1001 EV[TN02CD2222] slot=[0,1] entry=540
t1 == alias     : true
t1 == dup       : false
same ticketNo   : true
shared Vehicle? : false (copy constructor made a new Vehicle)

Creating 2000000 tickets (ms): constructor=45, default+setters=25, static factory=29 (sink 5999997000000)
```

**Test cases.** `==` on alias vs copy; copy constructor makes a new `Vehicle`.

**Complexity analysis.** Constructors, default+setters and static factory are all O(n) time and O(n) space for n tickets; constructors guarantee a valid object. Measured timings are dominated by JVM warm-up.

**Result.** Constructors justified and demonstrated.

---

# Task 5 – Apply static Members and the Object Life Cycle

**Aim.** Use class-level data correctly and explain object lifetime.

**Problem analysis.** Static ticket counter, static occupancy, static block, static method; ticket eligible for GC once the last reference is dropped; objects passed by reference value.

**Algorithm / pseudocode.** Not required.

**Implementation details and Java code references.** `Task05_Static_Members/TicketCounter.java`, `Explanation.md`.

**Output evidence.**
```
=== Static counter shared by all tickets ===
CP-1000(TN01AB1111,open) CP-1001(TN02CD2222,open) CP-1002(TN03EF3333,open)
Active tickets = 3

=== Object passing ===
CP-1001(TN02CD2222,closed)  active=2
Re-issued: CP-1003(TN03EF3333,open)  active=3

=== Object life cycle / garbage collection ===
Closed but still referenced -> reachable: true
After temp = null and System.gc() -> collected: true (usually true; GC timing is not guaranteed)

=== Alternative: instance counter with two facilities ===
north issues CP-1000, south issues CP-1000  -> DUPLICATE ticket number across the campus
Static counter issues CP-1005 then CP-1006 -> always unique
```

**Test cases.** Unique numbers; instance counter gives duplicate `CP-1000` for two facilities.

**Complexity analysis.** Static counter: 1 `int`; instance counters: k `int`s for k facilities; tickets O(n) either way.

**Result.** Static members justified; two-facility problem shown and solved.

---

# Task 6 – Design the Inheritance Hierarchy

**Aim.** Build `Vehicle → TwoWheeler / Car → ElectricCar` and trace constructors.

**Problem analysis.** Hierarchical + multilevel inheritance; `super` for constructors and methods; users are an enum, not a hierarchy.

**Algorithm / pseudocode.** Constructor trace table in `Explanation.md`.

**Implementation details and Java code references.** `Task06_Inheritance/*.java`, `UML_Diagram.md`.

**Output evidence.**
```
new TwoWheeler("TN01A1"):
  [1] Vehicle(String) constructor ran for TN01A1
  [2] TwoWheeler(String) constructor ran
new Car("TN02B2"):
  [1] Vehicle(String) constructor ran for TN02B2
  [2] Car(String) constructor ran
new ElectricCar("TN03C3"):
  [1] Vehicle(String) constructor ran for TN03C3
  [2] Car(String) constructor ran
  [3] ElectricCar(String,int) constructor ran, battery=40
  [4] ElectricCar(String) constructor ran

Vehicle TN01A1 rate Rs 10.0/hour
Vehicle TN02B2 rate Rs 30.0/hour, category Car
Vehicle TN03C3 rate Rs 30.0/hour, category Car, electric, battery 40 kWh
Inherited getRegNo() on ElectricCar: TN03C3
e instanceof Car     : true
e instanceof Vehicle : true
```

**Test cases.** Constructor order [1] Vehicle → [2] Car → [3] ElectricCar(String,int) → [4] ElectricCar(String).

**Complexity analysis.** Inheritance vs composition: both O(1) per fee; composition adds a type reference per object but needs a new object (not class) per rate-only type.

**Result.** Hierarchy, UML and trace delivered.

---

# Task 7 – Override and Specialise Behaviour

**Aim.** Override fee behaviour and the Object methods.

**Problem analysis.** `ratePerHour()` overridden in each subclass; `Car.calculateFee` adds the daily cap; `final` on regNo, `getRegNo()` and constants; `equals/hashCode` by registration number.

**Algorithm / pseudocode.** Not required.

**Implementation details and Java code references.** `Task07_Method_Overriding/*.java`.

**Output evidence.**
```
TwoWheeler[TN01A1, Rs 10.0/h] fee for 600 min = 100.0
Car[TN02B2, Rs 30.0/h] fee for 600 min = 240.0  (capped at 240 by the Car override)
a == b          : false
a.equals(b)     : true
same hashCode   : true
a.equals(other) : false
a.equals(null)  : false
add first TN02B2 : true
add second TN02B2: false  (duplicate rejected)
Set size = 1
toString: [Car[TN02B2, Rs 30.0/h]]
```

**Test cases.** equals/hashCode contract; HashSet rejects the duplicate.

**Complexity analysis.** `switch` on type vs overriding: O(1) vs O(k) worst case for if–else; a new type touches 1 new class vs editing the shared method.

**Result.** Overriding, `final` and Object methods correct.

---

# Task 8 – Apply Polymorphism

**Aim.** Demonstrate overloading and dynamic dispatch; complete `FeeDemo`.

**Problem analysis.** Overloads are chosen at compile time by declared types; `Vehicle[]` holds three classes (upcasting); `calculateFee` and `ratePerHour` are dispatched at run time. The last line is `true` because `equals` compares registration numbers only – appropriate for this domain (one number = one physical vehicle).

**Algorithm / pseudocode.** Dispatch trace table in `Explanation.md`.

**Implementation details and Java code references.** `Task08_Polymorphism/*.java` (TODOs: ceiling-hours fee, `Car` constructor and rate, `chargingFee` at Rs 12/kWh).

**Output evidence.**
```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```
The output equals the expected output of the assignment.

Dispatch trace (`OverloadDemo`):
```
fee(car, 130):
  -> fee(Vehicle, int) selected at compile time
  = 90.0
fee(car, 09:00, 11:10):
  -> fee(Vehicle, LocalTime, LocalTime) selected at compile time
  = 90.0

label(declared Vehicle holding a Car) = label(Vehicle)
label(declared Car)                   = label(Car)

Dynamic method dispatch trace for v.calculateFee(130):
  declared type = Vehicle, actual type = TwoWheeler, calculateFee found in Vehicle, ratePerHour() executed in TwoWheeler -> Rs 30.0
  declared type = Vehicle, actual type = Car, calculateFee found in Vehicle, ratePerHour() executed in Car -> Rs 90.0
  declared type = Vehicle, actual type = ElectricCar, calculateFee found in Vehicle, ratePerHour() executed in Car -> Rs 90.0
```

**Test cases.** 130 minutes: 30 / 90 / 90 + 120 = 210.

**Complexity analysis.** Dispatch O(1) vs instanceof O(k). Measured honestly: dispatch 512 ms vs instanceof 200 ms for 100 million calls with k = 3 – the chain was faster; the benefit of polymorphism is maintainability, not speed.

**Result.** Expected output reproduced; `true` explained and justified.

---

# Task 9 – Introduce Abstraction

**Aim.** Separate what the system does from how.

**Problem analysis.** `Vehicle` abstract class (shared state, constructor, abstract `ratePerHour`); `Chargeable` and `Payable` interfaces (capabilities); default and static interface methods; `ElectricCar` combines a superclass with two interfaces.

**Algorithm / pseudocode.** Not required.

**Implementation details and Java code references.** `Task09_Abstraction/*.java`.

**Output evidence.**
```
TwoWheeler TN01A1 parking fee=30.0, visitor pays=30.0, pass holder pays=0.0
Car TN02B2 parking fee=90.0, visitor pays=90.0, pass holder pays=0.0
ElectricCar TN03C3 parking fee=90.0, visitor pays=90.0, pass holder pays=0.0
   charging valid? true, charging=120.0, with service charge=125.0
Chargeable.isValidKwh(500) = false
```

**Test cases.** Visitor vs pass-holder payment; default method 125.0; static method true/false.

**Complexity analysis.** Swapping roles (interface Vehicle, abstract Chargeable) loses state, constructor, `final`, and the single `extends`; per-call time and per-object space are essentially unchanged.

**Result.** Abstract class and interfaces justified.

---

# Task 10 – Enforce Encapsulation through Packages and Access Modifiers

**Aim.** Organise classes in packages and protect state.

**Problem analysis.** `parking.model`, `parking.service`, `parking.app`; private fields, protected constructor/`ratePerHour`, package-private helper, public API; validating setters; wrapper classes and the `Integer ==` trap.

**Algorithm / pseudocode.** Not required.

**Implementation details and Java code references.** `Task10_Encapsulation_Packages/parking/**`.

**Output evidence.**
```
Vehicle=TN02B2 floor=1 slot=5 minutes=130
Total fee = 90.0
negative duration: rejected -> Duration cannot be negative: -30
invalid slot: rejected -> Invalid slot: 250
invalid floor: rejected -> Invalid floor: 9
bad reg number: rejected -> Invalid registration number: 123
negative fee request: rejected -> Duration cannot be negative: -1

autoboxed/unboxed = 130
127 == 127 (Integer)   : true   (cached objects)
1000 == 1000 (Integer) : false  (different objects - the == trap)
1000 equals 1000       : true
Unboxing null -> NullPointerException
Integer.parseInt("45") + 5 = 50
```

**Test cases.** Negative duration, invalid slot/floor, bad registration, negative fee request, `Integer` cache, unboxing `null`.

**Complexity analysis.** Public fields move the same O(1) check to c call sites instead of 1; no time penalty for encapsulation.

**Result.** Encapsulation enforced; invalid states rejected.

---

# Task 11 – Implement, Test and Defend the Design

**Aim.** Implement the complete system and evaluate it with evidence.

**Problem analysis.** Entry/exit, validation, categories, automatic allocation, unique tickets, monthly passes, visitor fees, duplicate prevention, availability display, invalid-input handling. Evaluation of encapsulation, inheritance, polymorphism, abstraction and one limitation (linear first-fit scan, no dedicated charging slots) is in `DesignEvaluation.md`.

**Algorithm / pseudocode.** Fee rule: started hours, daily maximum = 8 × rate per 24 h; pass check by `Pass.covers`.

**Implementation details and Java code references.** `Task11_Implementation_Testing/src/parking/**` (877 lines); data in `input/`, results in `output/`.

**Output evidence.**
Scenario run (`input/scenario.txt`):
```
> PASS_MONTHLY TN01AB1001 STUDENT 2026-11-01 2026-11-30
OK: Monthly pass (STUDENT) TN01AB1001 valid 2026-11-01 to 2026-11-30
> ENTER TN01AB1001 CAR STUDENT 2026-11-23T08:00
OK: T0001 issued to TN01AB1001 (Car) slot floor 0 / slot 0
> ENTER TN02CD2002 TWOWHEELER VISITOR 2026-11-23T08:10
OK: T0002 issued to TN02CD2002 (TwoWheeler) slot floor 0 / slot 1
> ENTER TN03EF3003 EV STAFF 2026-11-23T09:00
OK: T0003 issued to TN03EF3003 (ElectricCar) slot floor 0 / slot 2
> ENTER TN04GH4004 CAR VISITOR 2026-11-23T09:30
OK: T0004 issued to TN04GH4004 (Car) slot floor 0 / slot 3
> ENTER TN07LM7007 CAR VISITOR 2026-11-23T08:00
OK: T0005 issued to TN07LM7007 (Car) slot floor 0 / slot 4
> ENTER TN02CD2002 CAR VISITOR 2026-11-23T09:40
ERROR: Duplicate registration number: TN02CD2002 is already parked
> ENTER 12AB CAR VISITOR 2026-11-23T09:40
ERROR: Invalid registration number: 12AB
> ENTER TN06JK6006 BUS VISITOR 2026-11-23T09:40
ERROR: Unknown vehicle category: BUS
> STATUS
Floor 0: 45/50 free
Floor 1: 50/50 free
Floor 2: 50/50 free
Total: 145/150 free
> EXIT TN02CD2002 2026-11-23T10:10
OK: T0002 TN02CD2002 left after 120 min | parking Rs 20.00 + charging Rs 0.00 = Rs 20.00
> EXIT TN04GH4004 2026-11-23T12:31
OK: T0004 TN04GH4004 left after 181 min | parking Rs 120.00 + charging Rs 0.00 = Rs 120.00
> EXIT TN03EF3003 2026-11-23T11:00 10
OK: T0003 TN03EF3003 left after 120 min | parking Rs 60.00 + charging Rs 120.00 = Rs 180.00
> EXIT TN01AB1001 2026-11-23T17:00
OK: T0001 TN01AB1001 left after 540 min | parking Rs 0.00 (pass applied) + charging Rs 0.00 = Rs 0.00
> EXIT TN07LM7007 2026-11-23T18:00
OK: T0005 TN07LM7007 left after 600 min | parking Rs 240.00 + charging Rs 0.00 = Rs 240.00
> EXIT TN99ZZ9999 2026-11-23T18:00
ERROR: Vehicle is not parked: TN99ZZ9999
> STATUS
Floor 0: 50/50 free
Floor 1: 50/50 free
Floor 2: 50/50 free
Total: 150/150 free
> REPORT
Parking fees Rs 440.00 | Charging fees Rs 120.00 | Pass sales Rs 0.00 | TOTAL Rs 560.00 | vehicles still parked 0
```
Test run summary:
```
Result: 38 of 38 tests passed
```

**Test cases.** 38 tests (normal 10, boundary 10, invalid 17, scenario 1) with expected and actual values in `TestCases.md`. Hand-calculated collection Rs 560 = program output Rs 560.

**Complexity analysis.** `allocate` O(F·S); exit with a HashMap O(1) vs O(n) for array/ArrayList. Measured: finding the last of 100,000 tickets – array 362 ms, ArrayList 500 ms, HashMap 0.057 ms (200 lookups).

**Result.** All 38 tests pass; design recommended for retention.

---

# Task 12 – Re-evaluate and Adapt the Design (Day-4 Design Challenge)

**Aim.** Respond to e-bikes, delivery vans, visitor day pass and five unequal floors.

**Problem analysis.** A: impact analysis (`ImpactAnalysis.md`) – the jagged array, `VehicleFactory` registry, `Pass` interface and `CommandProcessor` table meant nothing had to be modified. B: evaluation of the original design – new types are new subclasses; pricing is distributed per class (trade-off); existing classes stay unchanged. C: decision RETAIN and EXTEND with `EBike`, `DeliveryVan`, `DayPass`, `ParkingApp12`. D: defence with numbers (below).

**Algorithm / pseudocode.** Van fee = normal car fee + ceil(overlapMinutes × carRate / 60), where overlap is the intersection of the stay with 09:00–17:00 on every day the stay touches (not a check of the entry time).

**Implementation details and Java code references.** `Task12_Design_Challenge/src/parking/**`; new files: `EBike` (22 lines), `DeliveryVan` (45), `DayPass` (34), `ParkingApp12` (55), `TestRunner12` (137).

**Output evidence.**
Existing-class check: 17 of 17 Task 11 files byte-identical (`check_unchanged.sh`).

Scenario end of output:
```
> STATUS
Floor 0: 20/20 free
Floor 1: 30/30 free
Floor 2: 40/40 free
Floor 3: 50/50 free
Floor 4: 60/60 free
Total: 200/200 free
> REPORT
Parking fees Rs 358.00 | Charging fees Rs 0.00 | Pass sales Rs 200.00 | TOTAL Rs 558.00 | vehicles still parked 0
```
Test run summary:
```
Result: 33 of 33 tests passed
```
Regression (Task 11 suite on Task 12 code):
```
Result: 38 of 38 tests passed
```

**Test cases.** 33 new tests: e-bike E1–E4, van V1–V13 (including entry before 9, exit after 5, boundaries at 09:00 and 17:00, midnight crossing), day pass D1–D6, floors F1–F5, regression R1–R4, scenario S1 (hand calculation Rs 358 + Rs 200 = Rs 558).

**Complexity analysis.** Adding a vehicle type: O(1) work, 1 new class + 1 registration line. Van fee: O(d) for a stay touching d calendar days. Allocation unchanged O(F·S).

**Result.** Design retained and extended: 0 existing classes edited, 156 new production lines, 33/33 + 38/38 tests passing; trade-offs (distributed pricing, van coupled to car, interpretation of 'double') stated in `UpdatedDesign.md`.

---

## References to files

All code, input files and run outputs are in the repository folders named `TaskNN_…`; explanations in `Explanation.md` (and the other `.md` files) of each task. UML: `UML_Diagram.md`. Screenshots: to be added by the student.
