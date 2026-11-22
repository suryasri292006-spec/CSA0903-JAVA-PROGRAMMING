#!/bin/sh
# Compares every Task 11 source file with its copy in Task 12 and lists the new files.
cd "$(dirname "$0")/.." || exit 1
changed=0
for f in $(cd Task11_Implementation_Testing/src && find . -name '*.java' | sort); do
  if cmp -s "Task11_Implementation_Testing/src/$f" "Task12_Design_Challenge/src/$f"; then
    echo "UNCHANGED $f"
  else
    echo "CHANGED   $f"; changed=$((changed+1))
  fi
done
echo "Existing files changed: $changed"
echo "New files:"
(cd Task12_Design_Challenge/src && find . -name '*.java' | sort) | while read f; do
  [ -f "Task11_Implementation_Testing/src/$f" ] || echo "  NEW $f"
done
