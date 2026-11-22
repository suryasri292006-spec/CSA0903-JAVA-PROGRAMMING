# Task 7 – Override and Specialise Behaviour

## What is overridden and why

| Method | Defined in | Overridden in | Why the subclass version is needed |
|---|---|---|---|
| `ratePerHour()` (abstract) | `Vehicle` | `TwoWheeler` (10), `Car` (30) | The only data that *must* differ per type. |
| `calculateFee(int)` | `Vehicle` | `Car` | Adds the Car-specific **daily maximum**: `Math.min(super.calculateFee(m), 240)`. `TwoWheeler` keeps the inherited rule. In the demo a car parked 600 minutes pays **240**, a two-wheeler **100**. |
| `toString()`, `equals()`, `hashCode()` | `Object` | `Vehicle` | Readable text, and a business definition of "same vehicle". |

## `@Override` and the rules of overriding

* `@Override` asks the compiler to **verify** that a method really overrides one – a misspelt name (`calculatefee`) or a wrong parameter type becomes a compile error instead of a silent new method.
* **Signature:** same name and **identical parameter types**.
* **Return type:** the same, or a *covariant* (sub)type for object returns.
* **Access:** may stay the same or become **wider** (`protected` → `public`), never narrower. `ratePerHour()` is `protected` in `Vehicle` and `protected` in the subclasses; `toString/equals/hashCode` are `public` as in `Object`.
* **Exceptions:** an override may not throw broader checked exceptions.
* `static`, `private` and `final` methods cannot be overridden (a `static` method with the same signature *hides* instead).

## Where `final` is used and why

| Use | Example | Reason |
|---|---|---|
| final **variable** (constant) | `public static final int GRACE_MINUTES`, `Car.DAILY_MAX` | A rule value must not change while the program runs. |
| final **field** | `private final String regNo` | A vehicle's registration number is its identity; once set in the constructor it cannot change. This is what makes `hashCode()` safe (a changing hash would lose the object inside a `HashSet`). |
| final **method** | `public final String getRegNo()` | No subclass may change how the identity is read. |
| final **class** | would be used for value classes such as `Receipt` (a `record` is implicitly final); `Vehicle` is *not* final because it exists to be extended. |

## `toString()`, `equals()` and `hashCode()`

```java
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Vehicle)) return false;
    return regNo.equals(((Vehicle) o).regNo);
}
@Override public int hashCode() { return regNo.hashCode(); }
```

* Two `Vehicle` objects with the **same registration number are equal**, even if they are different objects or even different subclasses.
* **Contract:** equal objects must have equal hash codes. Because `hashCode` uses the same field as `equals`, the contract holds, and a `HashSet<Vehicle>` rejects the second `TN02B2` (see `Output.txt`: `add second TN02B2: false`, `Set size = 1`). Overriding `equals` **without** `hashCode` would break this.
* `toString()` gives `Car[TN02B2, Rs 30.0/h]` instead of `Car@1b6d3586`.

Output of the demonstration: `Output.txt`.

---

# Alternative Concept and Complexity Analysis

**Alternative: one `calculateFee()` with a `switch` on a type field**

```java
double calculateFee(int minutes) {
    int hours = (minutes + 59) / 60;
    switch (type) {
        case "TWOWHEELER": return hours * 10;
        case "CAR":        return Math.min(hours * 30, 240);
        case "EV":         return Math.min(hours * 30, 240);
        default: throw new IllegalArgumentException("unknown type");
    }
}
```

| | Overriding (my design) | `switch` on a type field |
|---|---|---|
| Time per fee calculation with k types | **O(1)** – the JVM jumps through the method table | **O(1) for a switch on an int/enum** (jump table), **O(k) comparisons** if it is an if–else chain or a `switch` on Strings in the worst case |
| Classes/places that change when a **new type** is added | **1 new class** (the new subclass); no existing class is edited (open/closed principle) | **The one big method must be edited** (and every other method that also switches on `type`) – existing, already-tested code is modified |
| Risk of forgetting a case | Compiler forces the abstract method to be implemented | `default` branch hides the mistake until run time |
| Where the rules for one type live | All together in one class | Scattered over every `switch` |
| Space | One method table per class (shared by all objects) | One `type` field per object |

Number of classes touched for a new type: **1 (added)** versus **1 existing class modified** *plus* every other method with a `switch (type)`.

Time is practically identical; the real difference is **maintainability** – measured in Task 12, where adding e-bikes and vans required **0 changed** and 3 new model classes.
