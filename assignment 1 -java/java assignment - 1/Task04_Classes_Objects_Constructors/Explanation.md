# Task 4 – Classes, Objects and Constructors

## Classes in this task

| Class | Fields | Methods |
|---|---|---|
| `Vehicle` | `regNo`, `type` | getters, `toString()` |
| `ParkingTicket` | `ticketNo`, `vehicle`, `floor`, `slot`, `entryMinute` (all `final`) | getters, `toString()` |

(The final system – Task 11 – uses the richer `Vehicle` hierarchy of Tasks 6–9 and `LocalDateTime`; this task isolates the constructor ideas.)

## Constructors and when each is used

| Constructor | Used when |
|---|---|
| `Vehicle()` (default) | The vehicle is not yet known (placeholder). Chains to `this("UNKNOWN","CAR")`. |
| `Vehicle(String regNo)` (overloaded) | Type not supplied – defaults to `CAR`. Chains to the two-argument constructor. |
| `Vehicle(String regNo, String type)` (parameterised) | Normal creation. The **only** constructor that assigns fields. |
| `Vehicle(Vehicle other)` (copy) | Duplicating a vehicle record. |
| `ParkingTicket(int, Vehicle, int, int, int)` | Normal ticket with slot already allocated. |
| `ParkingTicket(int, Vehicle, int)` (overloaded) | Ticket before a slot is known: `this(..., -1, -1, ...)`. |
| `ParkingTicket()` (default) | A blank placeholder ticket. Not used by the real system. |
| `ParkingTicket(ParkingTicket other)` (copy) | **Re-issuing a duplicate ticket** (lost ticket): same number, same slot, but a **new independent** `Vehicle` copy so later changes to one do not affect the other. |

**`this` keyword** – (1) `this.regNo = regNo` separates the field from the parameter with the same name; (2) `this(...)` calls another constructor of the same class.

**Constructor chaining** – every short constructor delegates to the full one, so the assignment code (and later any validation) exists in **one** place. Without chaining the same four lines would be repeated in each constructor and a fix would have to be made in each.

**Copy constructor needed?** Yes for `ParkingTicket` (duplicate re-issue). A copy constructor is *not* needed for the immutable `Receipt` record.

## Memory: reference variables, stack and heap

```
STACK (main)                      HEAP
t1  ─────────────────────────►  ParkingTicket { 1001, vehicle ──┐, 0, 1, 540 }
alias ───────────────────────►  (same object as t1)              │
dup ─────────────────────────►  ParkingTicket { 1001, vehicle ──┐│ ... }
                                                                 ││
v3  ─────────────────────────►  Vehicle { "TN02CD2222", "EV" } ◄─┘│
                                Vehicle { "TN02CD2222", "EV" } ◄──┘   (copy made by copy constructor)
```

* Local variables (`t1`, `alias`, `dup`, `v3` …) are **references** stored on the stack; objects created with `new` live on the heap.
* `alias = t1` copies the reference only: `t1 == alias` is `true`. `dup` is a different object: `t1 == dup` is `false`, but their `ticketNo` is the same.
* `new Vehicle(other)` inside the copy constructor creates a second heap object, so `t1.getVehicle() == dup.getVehicle()` is `false` (see `Output.txt`).

## Output

`Output.txt` (program `Task04Demo`): all four `Vehicle` constructors, all four `ParkingTicket` constructors, the `==` experiments and the timing of ticket creation.

---

# Alternative Concept and Complexity Analysis

**Alternatives to overloaded constructors + chaining**

1. **Default constructor + setters**
```java
MutableTicket m = new MutableTicket();
m.setTicketNo(5); m.setVehicle(v); m.setSlot(0, 3); m.setEntryMinute(600);
```
2. **Static factory method**
```java
FactoryTicket t = FactoryTicket.forVehicle(5, v, 0, 3);    // constructor is private
```

| | Constructors (my design) | Default + setters | Static factory |
|---|---|---|---|
| Is the object valid as soon as it exists? | **Yes** – no object can exist without all data | **No** – between `new` and the last setter the object is half-built; forgetting a setter leaves a wrong default (0 / null) | **Yes** – the factory validates before calling the private constructor |
| Can fields be `final` (immutable ticket)? | Yes | No (setters must modify them) | Yes |
| Readable names (`forVehicle`, `reissue`) | No – overloads differ only by parameter types | – | **Yes** |
| Can return a cached/shared object or a subtype | No | – | **Yes** |
| Time to create n tickets | O(n) | O(n) (n × 4 extra method calls) | O(n) (one extra static call) |
| Space for n tickets | n × object size | n × object size | n × object size |

All three are **O(n) time and O(n) space** – the choice is about *correctness*, not speed. Measured for n = 2,000,000 (`Output.txt`): constructor = 45 ms, default + setters = 25 ms, static factory = 29 ms. The ordering is **not** meaningful: the first loop includes JVM warm-up, and after JIT inlining a setter call costs almost nothing. Constructors guarantee a valid object, so they are the right choice for `ParkingTicket`.
