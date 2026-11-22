# Task 1 – Understand the Problem and Identify the Objects

## 1. Inputs and outputs of the system

| Inputs | Outputs |
|---|---|
| Registration number of the vehicle | Allocated slot (floor and slot number) |
| Vehicle category (two-wheeler, car, EV; later e-bike, van) | Parking ticket with a unique ticket number |
| User type (student, staff, visitor) | Entry time and exit time recorded on the ticket |
| Entry time and exit time | Parking fee (and EV charging fee, shown separately) |
| Pass details (monthly pass for students/staff) | Free exit when a valid pass exists |
| EV charging energy in kWh (at exit) | Availability display (free slots per floor) |
| Facility layout (floors × slots) | Error message for invalid requests |
| | Daily collection report |

## 2. Nouns → candidate classes (and why each is needed)

| Noun in the problem | Class? | Justification |
|---|---|---|
| Vehicle | **Yes** – abstract `Vehicle` | Holds the registration number and the fee rule. Needed because the *fee depends on the vehicle type*, so behaviour (not only data) differs. |
| Two-wheeler, car, EV | **Yes** – `TwoWheeler`, `Car`, `ElectricCar` | Each has its own hourly rate. `ElectricCar` also has the charging capability. A single `type` field with `if–else` would put all pricing in one place that must be edited for every new type. |
| Parking ticket | **Yes** – `ParkingTicket` | Records *which* vehicle is in *which* slot since *when*, with a never-reused number. It is the link between entry and exit. |
| Slot, floor | **Yes (as data)** – `ParkingFacility` with `String[][]` | A slot has no behaviour of its own except "free or occupied", so a full `Slot` class would add objects without adding value. The facility owns the jagged array and the allocate/release logic. |
| Facility / parking system | **Yes** – `ParkingFacility`, `ParkingSystem` | `ParkingFacility` knows the slots; `ParkingSystem` coordinates entry, exit, tickets, passes and money. Separating them keeps the slot logic independent of the billing logic. |
| Pass (monthly) | **Yes** – `Pass` interface, `MonthlyPass` | A pass *waives* the fee for a period. Making it an interface lets other pass types (day pass in Task 12) be added without changing `ParkingSystem`. |
| User (student, staff, visitor) | **Enum** – `UserType` | The three kinds differ only in *whether a pass is allowed*, not in data or methods, so an enum is enough. Three subclasses would be over-design. |
| Charging slot / charging | **Interface** – `Chargeable` | Charging is a capability of some vehicles (not all), charged separately. |
| Receipt / bill | **Yes** – `Receipt` (record) | Immutable result of an exit: parking fee, charging fee, minutes, pass applied. |

## 3. Verbs → methods

| Verb | Method | Class |
|---|---|---|
| allocate a slot | `allocate(vehicleNo)` | `ParkingFacility` |
| release a slot | `release(floor, slot)` | `ParkingFacility` |
| issue a ticket | `new ParkingTicket(...)` (inside `enter`) | `ParkingTicket` / `ParkingSystem` |
| enter / leave | `enter(...)`, `exit(...)` | `ParkingSystem` |
| compute the fee | `calculateFee(minutes)` / `calculateFee(entry, exit)` | `Vehicle` and subclasses |
| charge an EV | `chargingFee(kWh)` | `Chargeable` / `ElectricCar` |
| register a pass | `registerPass(pass)` | `ParkingSystem` |
| check a pass | `covers(entry, exit)` | `Pass` |
| display availability | `availabilityReport()` | `ParkingSystem` |
| validate a number | `validateRegNo(text)` | `Vehicle` |

## 4. Responsibilities

* **Vehicle (and subclasses):** know their registration number (validated, normalised to upper case) and *how much they cost per hour*; compute their own fee.
* **ParkingTicket:** immutable record of one visit; generates the unique ticket number from a shared counter.
* **ParkingFacility:** only the slots – find the first free slot, free a slot, count free slots. It knows nothing about money.
* **ParkingSystem:** the use-cases. Rejects duplicates, asks the facility for a slot, creates tickets, applies passes, adds up the money collected.
* **VehicleFactory:** turns a category name into the right `Vehicle` object, and rejects unknown names.
* **Pass / MonthlyPass:** decide whether a stay is covered by the pass.
* **CommandProcessor / ParkingApp:** read text commands and print results (user interface only).

## 5. Business rules every valid operation must satisfy

1. A slot never holds two vehicles at the same time.
2. A ticket number is never reused (a shared counter only increases).
3. A vehicle (identified by registration number, case-insensitive) cannot be inside the facility twice – duplicates are rejected.
4. A registration number must match `2 letters + 2 digits + 1–3 letters + 1–4 digits` (e.g. `TN01AB1111`).
5. The vehicle category and user type must be known; otherwise the request is rejected.
6. Entry is refused when no slot is free; the facility state does not change.
7. Exit time must not be before entry time; a vehicle that is not parked cannot exit.
8. On exit the slot is released and becomes available to the next vehicle.
9. Every started hour is charged in full; one 24-hour block never costs more than 8 hours' charge (daily maximum).
10. A student or staff member with a valid monthly pass pays no *parking* fee; a visitor (or a pass-less user) pays the full fee.
11. EV charging is charged separately (Rs 12 per kWh) even when parking is free; only chargeable vehicles may be charged, and energy cannot be negative.
12. A monthly pass can only be issued to students or staff.

## 6. Assumptions

* Rates (the assignment gives none; these are chosen to reproduce the expected outputs of Task 8): two-wheeler **Rs 10/hour**, car **Rs 30/hour**, EV car **Rs 30/hour + Rs 12/kWh**.
* Daily maximum = 8 × the hourly rate for every full or partial 24 hours.
* A slot is a general slot (any vehicle may use any slot); first-fit allocation, lowest floor first. Dedicated charging slots are not modelled – charging is billed by kWh at exit.
* Time is recorded to the minute; seconds are ignored.
* The facility sizes used are the three in the assignment: 1 × 20, 3 × 50, 5 × 100 slots (and, in Task 12, five floors with different capacities).
* Monthly passes carry no purchase price inside this system (they are issued by the administration); the day pass of Task 12 costs Rs 100 (assumed).
* The system runs on a single thread (one gate operator at a time), so no locking is needed.
* Registration numbers are written without spaces or hyphens.
