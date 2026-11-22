# Task 9 – Abstraction

## What is abstract, what is an interface

| Type | Kind | Why this kind |
|---|---|---|
| `Vehicle` | **abstract class** | All vehicles share **state** (`regNo`), a **constructor** and **ready-made code** (`calculateFee`), but a plain "vehicle" is never parked – only a car, bike, …. The class cannot be instantiated (`new Vehicle("X")` does not compile) and declares one **abstract method** `ratePerHour()`: the *what* (every vehicle has a rate) is fixed, the *how* (which number) is left to the subclass. |
| `Chargeable` | **interface** | Charging is a **capability** that some vehicles have and others do not; it is unrelated to the `Vehicle` family (a future charging kiosk could implement it too). Interfaces have no state and no constructor, which is exactly right for a pure capability. |
| `Payable` | **interface** | Describes *a way of paying* (`amountPayable`). `Visitor` pays the full fee, `PassHolder` pays 0 – two different implementations behind one contract. |

## Why an abstract class in one place and an interface in another

* **Abstract class** – an *is-a* relationship with shared fields/constructor/behaviour (the family of vehicles).
* **Interface** – a *can-do* relationship (capability) that can be added to unrelated classes and **combined**, because a class may extend only **one** class but implement **many** interfaces.

## Multiple behaviours through interfaces

```java
public class ElectricCar extends Car implements Chargeable, Comparable<Vehicle> { ... }
```

`ElectricCar` gets its *identity and fee logic* from one parent (`Car → Vehicle`) and two extra behaviours from interfaces: it can be **charged** and it can be **sorted** by registration number. Java cannot do this with abstract classes alone (no multiple class inheritance).

## `default` and `static` methods in an interface (`Chargeable`)

* **`default double chargingFeeWithServiceCharge(int kWh)`** – gives every implementer a ready-made method built on the abstract one (`chargingFee(kWh) + 5`). Useful for **adding a method to an existing interface without breaking** the classes that already implement it.
* **`static boolean isValidKwh(int kWh)`** – a utility that belongs to the interface itself and is called as `Chargeable.isValidKwh(10)`; it is not inherited by implementers.
* `double RATE_PER_KWH = 12;` – interface fields are automatically `public static final` constants.

Output (`Output.txt`): fees for the three vehicles, visitor vs pass-holder payment, the default method (125.0 = 120 + 5), the static method (`true` for 10 kWh, `false` for 500).

---

# Alternative Concept and Complexity Analysis

**Alternative:** `Vehicle` becomes an **interface** with default methods, and `Chargeable` becomes an **abstract class**.

```java
interface Vehicle { String getRegNo();  double ratePerHour();
                    default double calculateFee(int m) { return ((m + 59) / 60) * ratePerHour(); } }
abstract class Chargeable { abstract double chargingFee(int kWh); }
class ElectricCar extends Chargeable implements Vehicle { ... }      // already uses up the only "extends"
```

**What can no longer be expressed**

| Feature | Consequence |
|---|---|
| **State** in `Vehicle` (`private final String regNo`) | Interfaces cannot have instance fields; every implementing class must declare its own `regNo` and implement `getRegNo()` – the code is duplicated, and nothing guarantees it is validated or immutable. |
| **Constructor** (validation of the registration number in one place) | Interfaces have no constructors; validation would have to be repeated or moved to a helper that each class must remember to call. |
| **`protected` members, `final` methods** | Interface methods are public; a `default` method can be overridden by any class, so the fee rule can no longer be protected with `final`. |
| **Multiple inheritance of behaviour** | `ElectricCar extends Chargeable` uses up the single `extends`, so `ElectricCar` can no longer extend `Car`; reuse of `Car`'s code is lost (or must be copied). Any other capability (a "Payable"-style one) would also have to be an interface, so the abstract-class version of `Chargeable` buys nothing. |
| `equals/hashCode/toString` as `default` methods | **Not allowed** – an interface cannot provide default implementations of `Object` methods. |

**Time and space**

* **Time per method call:** interface calls use *interface dispatch* (itable lookup) versus virtual dispatch (vtable) for class methods; both are O(1), and the JIT compiler largely removes the difference. No meaningful difference was measured or is expected.
* **Space per object:** the same object header; the interface version needs **no extra memory per object** for the methods, but each implementing class carries its *own* `regNo` field (same total: one reference per object). Per-class metadata (method tables) is O(number of classes), not per object.

The design with an abstract `Vehicle` and an interface `Chargeable` is therefore the better fit: state and validation are written once, and capabilities remain combinable.
