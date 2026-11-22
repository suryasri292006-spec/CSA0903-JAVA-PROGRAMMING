# Task 11 – Test Cases (expected vs actual)

All 38 tests are executed by `parking.app.TestRunner`; the genuine run is saved in `output/test_results.txt` (**38 of 38 passed**). Money is in rupees. Rates: two-wheeler 10/h, car 30/h (EV car 30/h + Rs 12/kWh), daily maximum = 8 × hourly rate.

## Normal operation

| ID | Test | Expected | Actual | Result |
|---|---|---|---|---|
| N1 | Two-wheeler 120 min | 20.0 | 20.0 | PASS |
| N2 | Car 130 min (3 started hours) | 90.0 | 90.0 | PASS |
| N3 | EV 130 min + 10 kWh (90 + 120) | 210.0 | 210.0 | PASS |
| N4 | Two entries get different ticket numbers | true | true | PASS |
| N5 | Slot released on exit and reused | 0,0 | 0,0 | PASS |
| N6 | Student with valid monthly pass, 4 h car | 0.0 | 0.0 | PASS |
| N7 | Visitor car 60 min | 30.0 | 30.0 | PASS |
| N8 | Staff without a pass pays | 30.0 | 30.0 | PASS |
| N9 | Expired pass does not waive the fee | 30.0 | 30.0 | PASS |
| N10 | Availability display after one entry | true | true | PASS |

## Boundary cases

| ID | Test | Expected | Actual | Result |
|---|---|---|---|---|
| B1 | Zero-minute parking | 0.0 | 0.0 | PASS |
| B2 | Exactly 60 min (slab boundary) | 30.0 | 30.0 | PASS |
| B3 | 61 min (next slab) | 60.0 | 60.0 | PASS |
| B4 | Exactly 480 min (daily maximum reached) | 240.0 | 240.0 | PASS |
| B5 | 481 min (still capped) | 240.0 | 240.0 | PASS |
| B6 | Exactly 1440 min | 240.0 | 240.0 | PASS |
| B7 | 1441 min (one day + 1 hour) | 270.0 | 270.0 | PASS |
| B8 | Two-wheeler 600 min (cap = 80) | 80.0 | 80.0 | PASS |
| B9 | **Last free slot**, then facility full | 0,0 then `IllegalStateException` | same | PASS |
| B10 | Pass valid on its last day | 0.0 | 0.0 | PASS |

## Invalid cases

| ID | Test | Expected | Actual | Result |
|---|---|---|---|---|
| I1 | Unknown vehicle type BUS | IllegalArgumentException | same | PASS |
| I2 | Duplicate registration number (also different case) | IllegalStateException | same | PASS |
| I3 | Invalid registration format `12AB` | IllegalArgumentException | same | PASS |
| I4 | Null registration number | IllegalArgumentException | same | PASS |
| I5 | Release slot of invalid floor 5 | false | false | PASS |
| I6 | Release negative slot index | false | false | PASS |
| I7 | Release a slot already free | false | false | PASS |
| I8 | Exit of a vehicle that never entered | IllegalArgumentException | same | PASS |
| I9 | Exit before entry time | IllegalArgumentException | same | PASS |
| I10 | Negative kWh | IllegalArgumentException | same | PASS |
| I11 | Charging requested for a non-EV | IllegalArgumentException | same | PASS |
| I12 | Monthly pass for a visitor | IllegalArgumentException | same | PASS |
| I13 | Unknown user type GUEST | IllegalArgumentException | same | PASS |
| I14 | Negative duration | IllegalArgumentException | same | PASS |
| I15 | Unknown command | `ERROR: Unknown command: FLY` | same | PASS |
| I16 | Invalid date text | `ERROR: Invalid date/time: yesterday` | same | PASS |
| I17 | Missing command arguments | `ERROR: Missing arguments for ENTER` | same | PASS |

## Daily-collection scenario (hand calculation vs program)

Input: `input/scenario.txt`; program output: `output/scenario_output.txt`. Date 23 Nov 2026. Three invalid requests in the file (duplicate, bad number, unknown type) are rejected and change nothing.

| Vehicle | Stay | Minutes | Billable hours | Calculation | Parking | Charging |
|---|---|---|---|---|---|---|
| TN01AB1001 car, student **with monthly pass** | 08:00–17:00 | 540 | 9 → capped | pass applies | **0** | 0 |
| TN02CD2002 two-wheeler, visitor | 08:10–10:10 | 120 | 2 | 2 × 10 | **20** | 0 |
| TN03EF3003 EV, staff (no pass), 10 kWh | 09:00–11:00 | 120 | 2 | 2 × 30 = 60; 10 × 12 = 120 | **60** | **120** |
| TN04GH4004 car, visitor | 09:30–12:31 | 181 | 4 (181/60 = 3.02 → 4) | 4 × 30 | **120** | 0 |
| TN07LM7007 car, visitor | 08:00–18:00 | 600 | 10 → 300 > 240 | daily maximum | **240** | 0 |
| **Totals** | | | | | **440** | **120** |

**Hand-calculated total collection = 440 + 120 = Rs 560.**
**Program output:** `Parking fees Rs 440.00 | Charging fees Rs 120.00 | Pass sales Rs 0.00 | TOTAL Rs 560.00 | vehicles still parked 0` → **matches** (test S1 repeats this check automatically).

The two `STATUS` lines in the output also confirm slot handling: 145/150 free after five vehicles are inside, 150/150 free after all have left.

## How to run

```
javac -d out $(find src -name "*.java")
java -cp out parking.app.TestRunner                          # run from this folder (it reads input/scenario.txt)
java -cp out parking.app.ParkingApp input/scenario.txt
java -cp out parking.app.ParkingApp < input/interactive_input.txt     # keyboard mode with a prepared input
```
