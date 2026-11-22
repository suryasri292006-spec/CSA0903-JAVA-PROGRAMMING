# Task 12 – Test Cases

Executed by `parking.app.TestRunner12`; genuine output in `output/test_results_task12.txt` (**33 of 33 passed**). Regression: the unchanged Task 11 suite on the Task 12 code, `output/regression_task11_tests_on_task12_code.txt` (**38 of 38 passed**). Rates: car 30/h, daily maximum 240, e-bike flat Rs 20, day pass Rs 100.

## E-bike (flat fee)

| ID | Test | Expected | Actual |
|---|---|---|---|
| E1 | 0 minutes | 20.0 | 20.0 |
| E2 | 30 minutes | 20.0 | 20.0 |
| E3 | 300 minutes | 20.0 | 20.0 |
| E4 | negative duration | IllegalArgumentException | same |

## Delivery van (only the overlap with 09:00–17:00 is doubled)

| ID | Stay | Normal fee | Overlap (min) | Surcharge | Expected | Actual |
|---|---|---|---|---|---|---|
| V1 | 10:00–12:00 | 60 | 120 | 60 | 120.0 | 120.0 |
| V2 | 07:00–08:00 | 30 | 0 | 0 | 30.0 | 30.0 |
| V3 | **08:00–10:00 (enters before 9)** | 60 | 60 | 30 | **90.0** | 90.0 |
| V4 | **16:00–18:00 (leaves after 5)** | 60 | 60 | 30 | **90.0** | 90.0 |
| V5 | 17:00–18:00 (starts at window end) | 30 | 0 | 0 | 30.0 | 30.0 |
| V6 | 08:00–09:00 (ends at window start) | 30 | 0 | 0 | 30.0 | 30.0 |
| V7 | 09:00–17:00 (whole window) | 240 (cap) | 480 | 240 | 480.0 | 480.0 |
| V8 | 08:30–09:30 | 30 | 30 | 15 | 45.0 | 45.0 |
| V9 | 16:45–17:15 | 30 | 15 | ceil(7.5) = 8 | 38.0 | 38.0 |
| V10 | 23 Nov 16:00 – 24 Nov 10:00 | 240 (18 h, cap) | 60 + 60 | 60 | 300.0 | 300.0 |
| V11 | 10:00–10:00 (zero minutes) | 0 | 0 | 0 | 0.0 | 0.0 |
| V12 | *Car* 10:00–12:00 (baseline, no surcharge) | 60 | – | – | 60.0 | 60.0 |
| V13 | `peakMinutes` for 08:00–18:00 | – | 480 | – | 480 | 480 |

## Day pass

| ID | Test | Expected | Actual |
|---|---|---|---|
| D1 | Visitor with day pass, same day | 0.0 | 0.0 |
| D2 | Pass for another date | 30.0 (pays) | 30.0 |
| D3 | Van stay crosses midnight, pass for the entry day | 300.0 (pays) | 300.0 |
| D4 | Pass sale added to collection | 100.0 | 100.0 |
| D5 | Null date | IllegalArgumentException | same |
| D6 | Invalid registration | IllegalArgumentException | same |

## Five floors with different capacities

| ID | Test | Expected | Actual |
|---|---|---|---|
| F1 | Vehicle 2 with floors {1,2,3,4,5} | floor 1 slot 0 | 1,0 |
| F2 | Vehicle 4 | floor 2 slot 0 | 2,0 |
| F3 | Vehicle 15 (last slot) | floor 4 slot 4 | 4,4 |
| F4 | Vehicle 16 | IllegalStateException | same |
| F5 | Production layout {20,30,40,50,60} | 5 floors / 200 slots | 5/200 |

## Regression and scenario

| ID | Test | Expected | Actual |
|---|---|---|---|
| R1 | Car 130 min | 90.0 | 90.0 |
| R2 | EV 130 min + 10 kWh | 210.0 | 210.0 |
| R3 | Unknown category BUS | IllegalArgumentException | same |
| R4 | Duplicate registration | IllegalStateException | same |
| S1 | Scenario collection, hand calculation 358 + 200 | 558.0 | 558.0 |

## Scenario by hand (`input/scenario12.txt`)

| Vehicle | Stay | Fee |
|---|---|---|
| TN10AA1001 car, visitor, **day pass** for 23 Nov | 09:00–15:00 | 0 |
| TN11BB1002 e-bike | 08:00–10:00 | 20 |
| TN12CC1003 van | 08:00–10:00 | 60 + 30 = 90 |
| TN13DD1004 van | 10:00–12:00 | 60 + 60 = 120 |
| TN14EE1005 van | 16:45–17:15 | 30 + 8 = 38 |
| TN15FF1006 car | 10:00–12:00 | 60 |
| TN16GG1007 car, day pass for **24 Nov** (wrong date) | 09:00–10:00 | 30 |
| **Parking fees** | | **358** |
| Pass sales (2 day passes × Rs 100) | | **200** |
| **Total collection** | | **Rs 558** |

Program: `Parking fees Rs 358.00 | Charging fees Rs 0.00 | Pass sales Rs 200.00 | TOTAL Rs 558.00` ✔. The three invalid commands in the file (unknown category BUS, duplicate e-bike, bad date) are rejected with `ERROR:` lines.

## How to run

```
javac -d out $(find src -name "*.java")
java -cp out parking.app.TestRunner12                       # new tests (run from this folder)
java -cp out parking.app.TestRunner                         # Task 11 regression tests on the new code
java -cp out parking.app.ParkingApp12 input/scenario12.txt
```
