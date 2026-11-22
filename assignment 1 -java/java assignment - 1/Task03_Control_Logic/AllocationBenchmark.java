import java.util.ArrayDeque;

/**
 * Task 3 - alternative concept: scan allocator (given ParkingFacility)
 * versus a free-slot stack with a free-count per floor.
 * Measures the worst case: the facility is almost full, so the scan must walk far.
 */
public class AllocationBenchmark {

    /** Alternative design: one stack of free slot numbers per floor + free counts. */
    static class StackFacility {
        private final String[][] slots;
        private final ArrayDeque<Integer>[] freeStack;
        private final int[] freeCount;

        @SuppressWarnings("unchecked")
        StackFacility(int[] slotsPerFloor) {
            slots = new String[slotsPerFloor.length][];
            freeStack = new ArrayDeque[slotsPerFloor.length];
            freeCount = new int[slotsPerFloor.length];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new String[slotsPerFloor[f]];
                freeStack[f] = new ArrayDeque<>();
                for (int s = slotsPerFloor[f] - 1; s >= 0; s--) freeStack[f].push(s); // lowest on top
                freeCount[f] = slotsPerFloor[f];
            }
        }

        int[] allocate(String no) {
            for (int f = 0; f < slots.length; f++) {      // O(F) over the counts only
                if (freeCount[f] == 0) continue;
                int s = freeStack[f].pop();               // O(1)
                slots[f][s] = no;
                freeCount[f]--;
                return new int[]{f, s};
            }
            return null;
        }

        boolean release(int f, int s) {
            if (f < 0 || f >= slots.length || s < 0 || s >= slots[f].length || slots[f][s] == null) return false;
            slots[f][s] = null;
            freeStack[f].push(s);                         // O(1)
            freeCount[f]++;
            return true;
        }
    }

    public static void main(String[] args) {
        int[][] configs = { {20}, {50, 50, 50}, {100, 100, 100, 100, 100} };
        String[] names = {"1 x 20", "3 x 50", "5 x 100"};
        int repeats = 200_000;

        System.out.println("Worst case: all slots but the last are occupied, then allocate+release the last slot "
                + repeats + " times");
        for (int c = 0; c < configs.length; c++) {
            int[] cfg = configs[c];
            ParkingFacility scan = new ParkingFacility(cfg);
            StackFacility stack = new StackFacility(cfg);
            int total = 0;
            for (int x : cfg) total += x;
            // fill everything except the very last slot in both designs
            for (int i = 0; i < total - 1; i++) { scan.allocate("V" + i); stack.allocate("V" + i); }
            int lastFloor = cfg.length - 1, lastSlot = cfg[lastFloor] - 1;

            long t0 = System.nanoTime();
            for (int i = 0; i < repeats; i++) { scan.allocate("X"); scan.release(lastFloor, lastSlot); }
            long scanMs = (System.nanoTime() - t0) / 1_000_000;

            t0 = System.nanoTime();
            for (int i = 0; i < repeats; i++) { stack.allocate("X"); stack.release(lastFloor, lastSlot); }
            long stackMs = (System.nanoTime() - t0) / 1_000_000;

            System.out.printf("%-9s slots=%-4d scan=%4d ms   stack+counts=%4d ms%n", names[c], total, scanMs, stackMs);
        }
    }
}
