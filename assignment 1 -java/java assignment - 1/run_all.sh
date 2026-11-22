#!/bin/sh
# Compiles and runs every task, regenerating the Output.txt / output/*.txt evidence files.
# Usage (Linux / macOS / Git Bash on Windows):   chmod +x run_all.sh && ./run_all.sh
# Requires JDK 17 or newer (javac and java on the PATH).
JAVAC=${JAVAC:-javac}
JFLAGS=${JFLAGS:---release 17}
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build"            # compiled classes go here (ignored by git)
rm -rf "$OUT"; mkdir -p "$OUT"
fail=0

run_single() {   # run_single <folder> <MainClass> <outputfile>   (default-package tasks)
  d="$ROOT/$1"; c="$2"; o="$3"
  mkdir -p "$OUT/$1"
  ( cd "$d" && $JAVAC $JFLAGS -d "$OUT/$1" *.java ) || { echo "COMPILE FAILED: $1"; fail=1; return; }
  ( cd "$d" && java -cp "$OUT/$1" "$c" > "$o" ) && echo "OK  $1 : $c -> $o" || { echo "RUN FAILED: $1 $c"; fail=1; }
}

run_single Task01_Problem_Analysis      ParallelArrayVsObjects  ParallelArrayVsObjects_Output.txt
run_single Task02_Data_Representation   DataRepresentation      Output.txt
run_single Task03_Control_Logic         ParkingFacility         Output.txt
( cd "$ROOT/Task03_Control_Logic" && java -cp "$OUT/Task03_Control_Logic" FeeCalculator > FeeCalculator_Output.txt \
  && java -cp "$OUT/Task03_Control_Logic" AllocationBenchmark > AllocationBenchmark_Output.txt && echo "OK  Task03 extras" )
run_single Task04_Classes_Objects_Constructors Task04Demo       Output.txt
run_single Task05_Static_Members        TicketCounter           Output.txt
run_single Task06_Inheritance           InheritanceDemo         Output.txt
run_single Task07_Method_Overriding     OverrideDemo            Output.txt
run_single Task08_Polymorphism          FeeDemo                 Output.txt
( cd "$ROOT/Task08_Polymorphism" && java -cp "$OUT/Task08_Polymorphism" OverloadDemo > OverloadDemo_Output.txt \
  && java -cp "$OUT/Task08_Polymorphism" DispatchBenchmark > DispatchBenchmark_Output.txt && echo "OK  Task08 extras" )
run_single Task09_Abstraction           AbstractionDemo         Output.txt

# Task 10 - packages
d="$ROOT/Task10_Encapsulation_Packages"; mkdir -p "$OUT/Task10"
( cd "$d" && $JAVAC $JFLAGS -d "$OUT/Task10" $(find parking -name '*.java') ) || fail=1
( cd "$d" && java -cp "$OUT/Task10" parking.app.EncapsulationDemo > Output.txt ) && echo "OK  Task10"

# Task 11 - integrated system
d="$ROOT/Task11_Implementation_Testing"; mkdir -p "$OUT/Task11" "$d/output"
( cd "$d" && $JAVAC $JFLAGS -d "$OUT/Task11" $(find src -name '*.java') ) || fail=1
( cd "$d" && java -cp "$OUT/Task11" parking.app.ParkingApp input/scenario.txt > output/scenario_output.txt \
  && java -cp "$OUT/Task11" parking.app.ParkingApp < input/interactive_input.txt > output/interactive_output.txt \
  && java -cp "$OUT/Task11" parking.app.TestRunner > output/test_results.txt \
  && java -cp "$OUT/Task11" parking.app.TicketStoreBenchmark > output/TicketStoreBenchmark_output.txt && echo "OK  Task11" ) || { echo "Task11 FAILED"; fail=1; }

# Task 12 - extended system
d="$ROOT/Task12_Design_Challenge"; mkdir -p "$OUT/Task12" "$d/output"
( cd "$d" && $JAVAC $JFLAGS -d "$OUT/Task12" $(find src -name '*.java') ) || fail=1
( cd "$d" && java -cp "$OUT/Task12" parking.app.ParkingApp12 input/scenario12.txt > output/scenario12_output.txt \
  && java -cp "$OUT/Task12" parking.app.TestRunner12 > output/test_results_task12.txt \
  && java -cp "$OUT/Task12" parking.app.TestRunner > output/regression_task11_tests_on_task12_code.txt && echo "OK  Task12" ) || { echo "Task12 FAILED"; fail=1; }
sh "$d/check_unchanged.sh" | tail -8

[ $fail -eq 0 ] && echo "ALL TASKS COMPILED AND RAN" || echo "SOME STEPS FAILED"
exit $fail
