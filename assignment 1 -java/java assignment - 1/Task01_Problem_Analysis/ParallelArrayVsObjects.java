import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task 1 - alternative concept: parallel arrays regNo[], type[], entryTime[]
 * versus one Vehicle object per vehicle. Measures a search by registration number.
 */
public class ParallelArrayVsObjects {

    /** Object design: one record per vehicle. */
    static class VehicleRecord {
        final String regNo; final String type; final int entryTime;
        VehicleRecord(String r, String t, int e) { regNo = r; type = t; entryTime = e; }
    }

    static String reg(int i) {   // unique registration numbers such as TN01AA0001
        return "TN" + String.format("%02d", i / 676 % 100) + (char) ('A' + i / 26 % 26) + (char) ('A' + i % 26)
                + String.format("%04d", i % 10000);
    }

    // parallel-array search: returns the index or -1
    static int findParallel(String[] regNo, int n, String key) {
        for (int i = 0; i < n; i++) if (regNo[i].equals(key)) return i;
        return -1;
    }

    static VehicleRecord findObject(List<VehicleRecord> list, String key) {
        for (VehicleRecord v : list) if (v.regNo.equals(key)) return v;
        return null;
    }

    public static void main(String[] args) {
        int[] sizes = {1_000, 10_000, 100_000};
        int searches = 200;
        System.out.println("Worst-case search (key is the LAST vehicle), " + searches + " searches per size");
        System.out.printf("%-9s %-18s %-18s %-18s%n", "n", "parallel arrays", "object list", "HashMap (extra)");

        for (int n : sizes) {
            String[] regNo = new String[n]; String[] type = new String[n]; int[] entry = new int[n];
            List<VehicleRecord> list = new ArrayList<>();
            Map<String, VehicleRecord> map = new HashMap<>();
            for (int i = 0; i < n; i++) {
                regNo[i] = reg(i); type[i] = "CAR"; entry[i] = i % 1440;
                VehicleRecord r = new VehicleRecord(regNo[i], "CAR", i % 1440);
                list.add(r); map.put(r.regNo, r);
            }
            String key = regNo[n - 1];
            long sink = 0;

            long t = System.nanoTime();
            for (int s = 0; s < searches; s++) sink += findParallel(regNo, n, key);
            double p = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int s = 0; s < searches; s++) sink += findObject(list, key).entryTime;
            double o = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int s = 0; s < searches; s++) sink += map.get(key).entryTime;
            double h = (System.nanoTime() - t) / 1e6;

            System.out.printf("%-9d %-18s %-18s %-18s (sink %d)%n", n, String.format("%.2f ms", p),
                    String.format("%.2f ms", o), String.format("%.3f ms", h), sink);
        }
        System.out.println("\nAdding a new attribute (e.g. ownerName):");
        System.out.println("  parallel arrays : declare a new array, grow it everywhere, change every method that moves/deletes a vehicle");
        System.out.println("  objects         : add ONE field to Vehicle; existing code keeps working");
    }
}
