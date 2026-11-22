# Task 1 – Alternative Concept and Complexity Analysis

## Alternative: parallel arrays instead of Vehicle objects

```java
String[] regNo     = new String[MAX];     // regNo[i], type[i], entryTime[i] describe vehicle i
String[] type      = new String[MAX];
int[]    entryTime = new int[MAX];
int count = 0;

void addVehicle(String r, String t, int e) { regNo[count] = r; type[count] = t; entryTime[count] = e; count++; }

int find(String r) {                       // linear search
    for (int i = 0; i < count; i++) if (regNo[i].equals(r)) return i;
    return -1;
}
// fee: a switch on type[i] must be repeated wherever a fee is needed
```

Object design used in the project: `List<Vehicle>` / `Map<String, ParkingTicket>` where every vehicle is one object that knows its own fee.

## Comparison

| Aspect | Parallel arrays | Vehicle objects (my design) |
|---|---|---|
| Find a vehicle by registration number | O(n) linear scan (unsorted) | O(n) in a list; **O(1) average with a `HashMap` keyed by regNo** |
| Find after sorting by regNo | O(log n) binary search, but every insert/delete must shift **all three arrays** in step – O(n) | – |
| Space per vehicle | 3 array slots (2 references + 1 int) ≈ 12 bytes + the Strings themselves | 1 reference + 1 object header (≈12–16 bytes) + 3 fields; a little more per vehicle, but fully encapsulated |
| Add a new attribute (e.g. `ownerName`) | New array, and **every** method that adds, removes, shifts or copies a vehicle must be edited – easy to forget one and desynchronise the arrays | Add **one field** (and getter) to `Vehicle`; existing code keeps working |
| Fee by type | `switch(type[i])` repeated everywhere | `v.calculateFee(...)` – polymorphism |
| Type safety | `type[i]` is a free String – typos compile | Types are classes; the compiler checks them |

Big-O: search = **O(n)** for both designs (the object design becomes **O(1)** when a `HashMap` is added). Space = **O(n)** for both; only the constant differs.

## Measurements (`ParallelArrayVsObjects.java`, worst case: the key is the last vehicle, 200 searches)

| n | parallel arrays | object list | HashMap |
|---|---|---|---|
| 1,000 | 26.41 ms | 35.98 ms | 0.490 ms |
| 10,000 | 111.89 ms | 117.22 ms | 0.425 ms |
| 100,000 | 156.67 ms | 178.58 ms | 10.370 ms |

*(Output saved in `ParallelArrayVsObjects_Output.txt`; your numbers will differ.)*

**Reading the results honestly:** the linear scans of both designs are of the same order and grow with n (the n = 1,000 row is inflated by JVM warm-up, so it should not be compared with the n = 10,000 row). The object list is slightly slower because each element is one more pointer to follow. The `HashMap` is far faster and nearly independent of n; the 10.37 ms value at n = 100,000 is a measurement artefact (garbage collection / warm-up), the expected cost is O(1) per lookup. The decisive difference between the designs is **maintainability**, not search speed.
