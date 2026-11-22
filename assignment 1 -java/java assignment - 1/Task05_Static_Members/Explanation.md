# Task 5 – static Members and the Object Life Cycle

## Where `static` is needed

| Member | Why it belongs to the class, not to an object |
|---|---|
| `private static int nextNumber` | The next ticket number is shared by **all** tickets. As an instance variable every ticket would start from its own copy and numbers would repeat. |
| `private static int activeTickets` | Total occupancy is a property of the whole facility, not of one ticket. |
| `private static final String PREFIX` | A constant identical for every ticket. |
| `static String issueNumber()` | Needs no `TicketCounter` object – it only touches static data. Called as `TicketCounter.issueNumber()`. |
| `static { ... }` block | Runs **once** when the class is loaded: sets the prefix and the starting number 1000 before any ticket is created. |
| `ParkingTicket.counter` (Task 11) | The same idea in the final system: `String.format("T%04d", ++counter)`. |

Static methods cannot use `this` or instance fields directly – they work only with static data or objects passed in.

## Life of a `ParkingTicket` object

1. **Creation** – `new Ticket(reg)` allocates the object on the heap and takes the next number.
2. **Active** – the system keeps a reference to it (in the `activeTickets` map in Task 11).
3. **Exit** – the slot is released, the ticket is marked closed and the system **removes** its reference.
4. **Unreachable** – when no variable refers to it any more (here `temp = null`) it becomes *eligible* for garbage collection. The GC may collect it later; `System.gc()` is only a request. The demo proves the point with a `WeakReference`: while `temp` still refers to the closed ticket it is **reachable**; after `temp = null` and `System.gc()` the weak reference is cleared (normally – timing is up to the JVM). Output: `reachable: true` then `collected: true`.

## Object passing and returning

Java passes **everything by value**; for objects the value is the **reference**.

* `markClosed(Ticket t)` receives a copy of the reference, so `t.close()` changes the *same* object the caller holds – the caller sees `closed`.
* Reassigning the parameter inside a method (`t = new Ticket(...)`) would **not** affect the caller.
* `reissue(Ticket old)` returns a reference to a **new** heap object; the caller must store it.

## Output

`Output.txt` shows unique numbers CP-1000, CP-1001, CP-1002, the active count 3 → 2 → 3, object passing, the GC demonstration and the two-facility comparison.

---

# Alternative Concept and Complexity Analysis

**Alternative:** the counter is an **instance** variable of `ParkingFacility`.

* With **one** facility it works.
* With **two** facilities (north and south gates) each object has its **own** counter, so both issue `CP-1000` – demonstrated in `Output.txt` ("DUPLICATE ticket number across the campus"). A ticket number would no longer identify one ticket.

**Solutions**

| Case | Solution |
|---|---|
| Ticket numbers must be unique across the whole campus | Keep the counter `static` (shared), or inject one shared `TicketNumberGenerator` object into both facilities. |
| Each facility needs its own sequence | Instance counter, **plus** a facility prefix (`N-1000`, `S-1000`) so numbers stay globally unique. |

**Space** for k facility objects and n tickets:

| Design | Counter storage | Ticket storage | Total |
|---|---|---|---|
| Static counter | **1** `int` | n tickets | O(1) + O(n) = O(n) |
| Instance counters | **k** `int`s (one per facility) | n tickets | O(k) + O(n) |

The extra space is negligible (a few bytes per facility). The real cost of the instance counter is not memory but **correctness** (duplicate numbers). The trade-off of the static counter: it is global mutable state – harder to test (tests must not depend on absolute numbers; Task 11's tests check only uniqueness) and not thread-safe without `synchronized` or `AtomicInteger` (the system is single-threaded by assumption).
