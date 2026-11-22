# Task 3 – Pseudocode

> **Note on the standard:** the assignment asks for the *SIMATS Engineering – Standard for Algorithmic Pseudocode*. That document was not supplied with the assignment file, so the pseudocode below uses a conventional structured style (upper-case keywords, `BEGIN/END`, indentation, `←` for assignment, one action per line, explicit INPUT/OUTPUT). **Before submitting, compare it with the standard document and adjust keywords or layout if your faculty's standard differs.**

## Algorithm 1 – ALLOCATE_SLOT

```
ALGORITHM AllocateSlot
INPUT  : vehicleNo (text), slots[0..F-1][0..S_f-1]  (null = free)
OUTPUT : {floor, slot}  or  NULL when the facility is full
BEGIN
    IF vehicleNo is empty THEN
        REPORT error "vehicle number required"
        STOP
    END IF
    FOR floor ← 0 TO F-1 DO                       // lowest floor first
        FOR slot ← 0 TO S_floor-1 DO              // lowest slot first
            IF slots[floor][slot] ≠ NULL THEN
                CONTINUE                          // occupied: try the next slot
            END IF
            slots[floor][slot] ← vehicleNo        // first free slot found
            RETURN {floor, slot}                  // RETURN ends both loops
        END FOR
    END FOR
    RETURN NULL                                   // every slot was occupied
END
```

*Stop condition:* the search stops at the **first free slot** (early return). When no slot is free all F × S slots are examined and `NULL` is returned; the caller must then refuse entry and must **not** issue a ticket.

## Algorithm 2 – RELEASE_SLOT

```
ALGORITHM ReleaseSlot
INPUT  : floor, slot
OUTPUT : TRUE if a vehicle was removed, otherwise FALSE
BEGIN
    IF floor < 0 OR floor ≥ F THEN RETURN FALSE
    IF slot  < 0 OR slot  ≥ S_floor THEN RETURN FALSE
    IF slots[floor][slot] = NULL THEN RETURN FALSE      // already free
    slots[floor][slot] ← NULL
    RETURN TRUE
END
```

## Algorithm 3 – COMPUTE_FEE

```
ALGORITHM ComputeFee
INPUT  : vehicleType, minutes (whole minutes)
OUTPUT : fee in rupees, or -1 for invalid input
BEGIN
    SWITCH vehicleType
        CASE "TWOWHEELER" : rate ← 10
        CASE "CAR"        : rate ← 30
        CASE "EV"         : rate ← 30
        DEFAULT           : RETURN -1               // unknown type
    END SWITCH
    IF minutes < 0 THEN RETURN -1
    IF minutes = 0 THEN RETURN 0                    // nothing to charge
    dailyMax  ← 8 × rate
    fee       ← 0
    remaining ← minutes
    WHILE remaining > 0 DO                          // one pass per 24-hour block
        block ← MIN(remaining, 1440)
        hours ← (block + 59) DIV 60                 // integer ceiling, no decimals
        IF hours = 1 THEN
            blockFee ← rate                         // first-hour slab
        ELSE
            blockFee ← rate + (hours − 1) × rate    // each additional started hour
        END IF
        IF blockFee > dailyMax THEN blockFee ← dailyMax    // daily-maximum slab
        fee       ← fee + blockFee
        remaining ← remaining − block
    END WHILE
    RETURN fee
END
```

Use of `break / continue / return`: `continue` skips occupied slots without nesting the "assign" code inside an `if`; `return` leaves both loops at once (no flag variable needed); early `return`s in `ComputeFee` reject invalid input and zero minutes before any work is done.

## Hand trace of Algorithm 1 (facility {2, 1})

| Call | Slots after (floor 0 / floor 1) | Result |
|---|---|---|
| allocate A | A,– / – | {0,0} |
| allocate B | A,B / – | {0,1} |
| allocate C | A,B / C | {1,0} |
| allocate D | A,B / C | NULL (full) |
| release(0,1) | A,– / C | TRUE |
| release(0,1) | A,– / C | FALSE (already free) |
| release(5,0) | A,– / C | FALSE (invalid floor) |
| allocate D | A,D / C | {0,1} |
