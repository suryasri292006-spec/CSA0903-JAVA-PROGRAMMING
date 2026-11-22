# Task 8 – Polymorphism

## Complete the Code – what was filled in

Only the `TODO` bodies were replaced; the rest of the provided program (including `main`) is unchanged. The program was split into one file per class so it fits the required folder layout; it compiles and runs unchanged from the folder (`javac *.java`, `java FeeDemo`).

| TODO | Code | Check |
|---|---|---|
| 1 `Vehicle.calculateFee` | `int hours = (minutes + 59) / 60; return hours * ratePerHour();` | 130 min → (130+59)/60 = 3 hours; 0 min → 0 hours |
| 2 `Car` | `Car(String regNo) { super(regNo); }` and `ratePerHour()` returns `30` | 3 × 30 = 90 |
| 3 `ElectricCar.chargingFee` | `return kWh * 12;` | 10 kWh → 120 |

Actual output (`Output.txt`) = expected output:

```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```

* TwoWheeler: 3 hours × 10 = **30**
* Car: 3 hours × 30 = **90**
* ElectricCar: parking 3 × 30 = 90 **plus** charging 10 × 12 = 120 → **210** (`instanceof Chargeable c` is true only for the EV)

## Why the last line prints `true` – and is it correct?

```java
new Car("TN02B2").equals(new ElectricCar("TN02B2"))
```

`Vehicle.equals` is `o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo)`. The argument is an `ElectricCar`, which **is a** `Vehicle` (so `instanceof` is true), and both objects have the registration number `"TN02B2"`, so `String.equals` returns `true`. The **class** of the objects (`Car` vs `ElectricCar`) is never compared – only the registration number.

**Is this the right behaviour for the whole system? Yes – with the limits below.**

* A registration number identifies **one physical vehicle**. The same number cannot belong to a car *and* an electric car at the same time, so equal numbers mean "the same vehicle". This is exactly what the system needs for **duplicate prevention**: a vehicle that is already inside must not be allowed in again whatever category the gate operator typed.
* `hashCode()` uses the same field, so the equals/hashCode contract holds and `HashSet`/`HashMap` work correctly. The integrated system keys the active tickets by registration number and so uses the same identity.
* **Limits:** (1) it only works because the registration number is **normalised** (trim + upper case) and validated in the constructor – in the final system `"tn02b2"` and `"TN02B2"` are the same vehicle, but in this small demo they would not be. (2) The relation is symmetric only as long as no subclass overrides `equals` differently. (3) If two vehicles of different categories were ever recorded with the same number (a data-entry error), `equals` would *hide* the error rather than expose it – that is why the system rejects the second entry instead of storing both. (4) A stricter `getClass() == o.getClass()` comparison would give `false` here; it is the right choice only when the *category* is part of a vehicle's identity, which it is not in this domain.

## Compile-time polymorphism (overloading) – `OverloadDemo.java`

```java
static double fee(Vehicle v, int minutes)                      // chosen for fee(car, 130)
static double fee(Vehicle v, LocalTime in, LocalTime out)      // chosen for fee(car, 9:00, 11:10)
static String label(Vehicle v)   /   static String label(Car c)
```

The **compiler** picks the method from the *number and declared (static) types* of the arguments, before the program runs. `label(declaredVehicle)` prints `label(Vehicle)` even though the object is a `Car`, because overloads look at the declared type – a classic trap (see `OverloadDemo_Output.txt`).

## Runtime polymorphism – upcasting and dynamic dispatch

```java
Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };
```

One array of `Vehicle` references holds three different classes (**upcasting** – a subclass object is assigned to a superclass reference automatically).

**Trace of `v.calculateFee(130)` when `v` refers to the `ElectricCar`:**

| Step | What happens |
|---|---|
| Declared type | `Vehicle` – the compiler only checks that `Vehicle` has `calculateFee(int)` |
| Actual object type | `ElectricCar` (known only at run time) |
| Method executed | The JVM looks in `ElectricCar`, then `Car`, then `Vehicle` and runs the first `calculateFee(int)` it finds → `Vehicle.calculateFee` |
| Inside it | `ratePerHour()` is called on the *same object*; the dispatch repeats: `ElectricCar` has none → `Car.ratePerHour()` runs → 30 |
| Result | 3 × 30 = 90.0 (the charging fee is added by `FeeDemo` through `Chargeable`) |

`OverloadDemo_Output.txt` prints this trace for each of the three objects.

## Why this avoids long `if–else` chains

Without polymorphism the caller would test every type: `if (v instanceof TwoWheeler) … else if (v instanceof Car) …`. With dynamic dispatch the caller writes `v.calculateFee(m)` once; the **object chooses** the code. A new vehicle class is picked up automatically – the calling code does not change.

---

# Alternative Concept and Complexity Analysis

**Alternative: `instanceof` checks with casting**

```java
static double fee(Vehicle v, int minutes) {
    int hours = (minutes + 59) / 60;
    if (v instanceof ElectricCar) return hours * 30;   // most specific type MUST be tested first
    else if (v instanceof Car)    return hours * 30;
    else if (v instanceof TwoWheeler) return hours * 10;
    throw new IllegalArgumentException("unknown type");
}
```

| | Dynamic dispatch | `instanceof` chain |
|---|---|---|
| Time per call, k types | **O(1)** – one method-table lookup | **O(k)** worst case – up to k type tests (later types cost more) |
| Order of tests | irrelevant | matters (`ElectricCar` before `Car`, otherwise the EV branch is unreachable) |
| New vehicle type | one new subclass; callers unchanged | edit **every** chain in the program (fee, display, reports …); a forgotten place silently falls into `default` or throws at run time |
| Code size | one `calculateFee` | grows by one branch **per type per operation** |
| Compile-time help | abstract method forces implementation | none |

**Measured** (`DispatchBenchmark_Output.txt`, 100 million calls, k = 3): dynamic dispatch = **512 ms**, `instanceof` chain = **200 ms**.

**This is the opposite of the textbook expectation, and I report it as measured.** With only k = 3 types the chain performs at most three very cheap type comparisons and the JIT compiler can inline them completely, while the call site that sees three different receiver classes is no longer a simple monomorphic call and goes through a virtual-method lookup. So for a small k the theoretical advantage of O(1) over O(k) does **not** show up as a speed advantage; the cost difference is a few nanoseconds per call and irrelevant for a parking gate. The exact reason inside the JVM was not investigated here. Therefore the case for polymorphism rests on **maintainability and correctness** (code size, number of places to edit, compiler checking), not on speed.
