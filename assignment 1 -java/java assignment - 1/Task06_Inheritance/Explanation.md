# Task 6 – The Inheritance Hierarchy

## Hierarchy and type of inheritance

```
Vehicle (abstract)
 ├── TwoWheeler
 └── Car
      └── ElectricCar
```

* **Hierarchical inheritance:** `TwoWheeler` and `Car` both extend `Vehicle`.
* **Multilevel inheritance:** `Vehicle → Car → ElectricCar`.
* Java allows only **single** inheritance of classes; the extra capability of an EV (charging) is added by an *interface* (Task 8/9), not by a second superclass.

Why an `is-a` hierarchy is right: a car **is a** vehicle and an electric car **is a** car (it is parked and billed like a car and can be used wherever a `Car` or `Vehicle` is expected).

**Users (student, staff, visitor):** *no* class hierarchy is used. The three kinds differ only in *whether a pass may exist* – there is no different data or behaviour to inherit – so the enum `UserType` is the appropriate design. Subclasses would be inheritance used for labelling only.

## What is inherited and what is added

| Class | Inherits | Adds / specialises |
|---|---|---|
| `Vehicle` | – | `regNo` field (private), `getRegNo()`, abstract `ratePerHour()`, `describe()` |
| `TwoWheeler` | `regNo`, `getRegNo()`, `describe()` | implements `ratePerHour()` = 10 |
| `Car` | same | implements `ratePerHour()` = 30; extends `describe()` |
| `ElectricCar` | everything in `Car` (including `Vehicle`) | new field `batteryKWh`, `getBatteryKWh()`, extends `describe()` again |

`regNo` is `private`: subclasses cannot touch it directly; they read it through the inherited public `getRegNo()` – encapsulation survives inheritance.

## How `super` is used

| Use | Example |
|---|---|
| **Constructor** – calls the parent constructor (must be the first statement) | `super(regNo);` in `Car`, `TwoWheeler`, and `ElectricCar(String,int)` |
| **Method** – reuses the parent's version of an overridden method | `return super.describe() + ", category Car";` in `Car`; `ElectricCar.describe()` calls `Car.describe()`, which calls `Vehicle.describe()` |
| **Field** – reaches a hidden parent field (`super.field`). Not needed here because the only field is private and fields are not hidden. | – |

## Constructor execution trace for `new ElectricCar("TN03C3")`

`ElectricCar(String)` immediately calls `this(regNo, 40)`; the two-argument constructor calls `super(regNo)`. A constructor body runs **after** its parent constructor finishes, so the bodies run **from the top of the hierarchy down**, while the calls are made from the bottom up.

| Step | Call (made) | Body that finishes (printed) |
|---|---|---|
| 1 | `new ElectricCar("TN03C3")` → `ElectricCar(String)` starts | – |
| 2 | `this(regNo, 40)` → `ElectricCar(String,int)` starts | – |
| 3 | `super(regNo)` → `Car(String)` starts | – |
| 4 | `super(regNo)` → `Vehicle(String)` starts (`Object()` is called implicitly first) | **[1]** `Vehicle(String) constructor ran` |
| 5 | back in `Car` | **[2]** `Car(String) constructor ran` |
| 6 | back in `ElectricCar(String,int)`: `batteryKWh = 40` | **[3]** `ElectricCar(String,int) constructor ran, battery=40` |
| 7 | back in `ElectricCar(String)` | **[4]** `ElectricCar(String) constructor ran` |

The real output (`Output.txt`) prints exactly the lines `[1] … [4]` in this order:

```
new ElectricCar("TN03C3"):
  [1] Vehicle(String) constructor ran for TN03C3
  [2] Car(String) constructor ran
  [3] ElectricCar(String,int) constructor ran, battery=40
  [4] ElectricCar(String) constructor ran
```

UML diagram: `UML_Diagram.md`.

---

# Alternative Concept and Complexity Analysis

**Alternative: composition** – one `Vehicle` class that *has a* `VehicleType` object holding the rate:

```java
class VehicleType { final String name; final double rate; ... }
class Vehicle {
    private final String regNo;
    private final VehicleType type;            // has-a
    double calculateFee(int minutes) { return ((minutes + 59) / 60) * type.rate; }
}
// new category = new VehicleType("EBIKE", 0) : data, no new class
```

| | Inheritance (my design) | Composition |
|---|---|---|
| Add a new vehicle type with only a different **rate** | New subclass (≈ 4 lines) | One new `VehicleType` **object** – no new class, can even be read from a file |
| Add a type with different **behaviour** (e.g. van with time-dependent price) | Override a method in a subclass – natural | Rate data is not enough; the `VehicleType` would have to become a strategy object with its own subclasses (so the hierarchy reappears, one level down) |
| Change the type of a vehicle at run time | Impossible (class is fixed) | Possible (`setType`) |
| Time to compute a fee | O(1) – one virtual call + arithmetic | O(1) – one extra field dereference + arithmetic |
| Space per vehicle object | regNo reference + header | regNo reference + **type reference** (+4–8 bytes), but the `VehicleType` objects are **shared** (3 objects for all vehicles) |
| Compile-time safety (`ElectricCar` has `chargingFee`) | Yes – the type has the method | Needs `instanceof`/flags to know whether charging exists |

Both are **O(1) time** and **O(n) space** for n vehicles. Inheritance was chosen because the problem has genuinely different *behaviour* (EV charging, the van's time-based price in Task 12), not only different numbers; composition would be the better choice if types differed *only* in a rate. In this project the idea of composition is still used where it fits: pricing extras are handled by the `Pass` interface rather than by more `Vehicle` subclasses.
