import java.util.ArrayList;
import java.util.Arrays;

/**
 * Task 2 - Data representation for the Campus Smart-Parking System.
 * Shows the chosen data types, the casting / integer-division risk,
 * and three ways of storing floors and slots.
 */
public class DataRepresentation {

    public static void main(String[] args) {
        // ---------- 1. Data type chosen for each field ----------
        int slotNumber = 7;                    // small whole number, used as array index
        String vehicleNumber = "TN01AB1111";   // text: letters + digits, so String
        int entryMinuteOfDay = 9 * 60 + 15;    // 09:15 stored as minutes since midnight (0..1439)
        int exitMinuteOfDay = 11 * 60 + 40;    // 11:40
        int durationMinutes = exitMinuteOfDay - entryMinuteOfDay; // whole minutes

        System.out.println("Slot=" + slotNumber + ", Vehicle=" + vehicleNumber
                + ", Entry=" + entryMinuteOfDay + ", Exit=" + exitMinuteOfDay
                + ", Duration=" + durationMinutes + " min");

        // ---------- 2. Integer division / casting risk ----------
        double ratePerHour = 30;               // car rate, Rs per hour (double: money may be fractional)
        int minutes = 130;                     // 2 h 10 min

        double wrongFee1 = (minutes / 60) * ratePerHour;           // 130/60 = 2 (integer division!)
        double wrongFee2 = (int) (minutes / 60.0) * ratePerHour;   // cast truncates 2.17 to 2
        double proRata = minutes / 60.0 * ratePerHour;             // 65.0 : fair, but NOT the billing rule
        int billableHours = (minutes + 59) / 60;                   // ceiling using integer arithmetic only
        double correctFee = billableHours * ratePerHour;           // 3 * 30 = 90

        System.out.println("\n--- Integer division risk (130 minutes, Rs 30/hour) ---");
        System.out.println("Wrong   (minutes / 60) * rate        = " + wrongFee1);
        System.out.println("Wrong   (int)(minutes / 60.0) * rate = " + wrongFee2);
        System.out.println("Pro-rata minutes / 60.0 * rate       = " + proRata + " (not the billing rule)");
        System.out.println("Correct ((minutes + 59) / 60) * rate = " + correctFee);

        // ---------- 3. Narrowing cast loses data ----------
        double fractionalFee = 89.5;
        int truncated = (int) fractionalFee;           // 89 : the 0.5 is lost
        long rounded = Math.round(fractionalFee);      // 90 : explicit rounding policy
        System.out.println("\n(int) 89.5 = " + truncated + " , Math.round(89.5) = " + rounded);

        // ---------- 4. Representing floors and slots ----------
        int[] slotsPerFloor = {20, 40, 60, 80, 100};   // different capacity on every floor

        // (a) Jagged array: natural, each row has its own length
        String[][] jagged = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            jagged[f] = new String[slotsPerFloor[f]];
        }
        jagged[2][59] = "TN01AB1111";                  // floor 2, last slot

        // (b) One-dimensional array with a prefix-offset table
        int[] floorStart = new int[slotsPerFloor.length + 1];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            floorStart[f + 1] = floorStart[f] + slotsPerFloor[f];
        }
        String[] flat = new String[floorStart[slotsPerFloor.length]];
        int naiveIndex = 2 * slotsPerFloor[0] + 59;    // floor * slotsPerFloor + slot (WRONG for jagged)
        int correctIndex = floorStart[2] + 59;         // offset table (correct)
        flat[correctIndex] = "TN01AB1111";

        System.out.println("\nTotal slots = " + flat.length);
        System.out.println("Naive index (floor*S+slot with S=20) = " + naiveIndex
                + "  -> floor " + whichFloor(floorStart, naiveIndex) + ", slot "
                + (naiveIndex - floorStart[whichFloor(floorStart, naiveIndex)]) + " (WRONG slot, expected 59)");
        System.out.println("Correct index with offsets          = " + correctIndex
                + "  -> floor " + whichFloor(floorStart, correctIndex) + ", slot "
                + (correctIndex - floorStart[whichFloor(floorStart, correctIndex)]) + " (correct)");

        // (c) ArrayList<String[]> : one String[] per floor
        ArrayList<String[]> list = new ArrayList<>();
        for (int capacity : slotsPerFloor) {
            list.add(new String[capacity]);
        }
        list.get(2)[59] = "TN01AB1111";
        System.out.println("ArrayList floors = " + list.size()
                + ", floor 2 slot 59 = " + list.get(2)[59]);
        System.out.println("Jagged floor 2 slot 59 = " + jagged[2][59]);
        System.out.println("Capacities = " + Arrays.toString(slotsPerFloor));

        // ---------- 5. Small timing of locating a slot ----------
        int rounds = 20_000_000;
        long checksum = 0;
        long t0 = System.nanoTime();
        for (int i = 0; i < rounds; i++) {
            if (jagged[i % 5][i % 20] != null) checksum++;
        }
        long jaggedNs = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < rounds; i++) {
            if (flat[floorStart[i % 5] + (i % 20)] != null) checksum++;
        }
        long flatNs = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < rounds; i++) {
            if (list.get(i % 5)[i % 20] != null) checksum++;
        }
        long listNs = System.nanoTime() - t0;

        System.out.println("\nTime for " + rounds + " slot look-ups (ms): jagged="
                + jaggedNs / 1_000_000 + ", flat+offsets=" + flatNs / 1_000_000
                + ", ArrayList=" + listNs / 1_000_000 + "  (checksum " + checksum + ")");
    }

    /** Finds the floor that owns a flat index (linear scan of the offset table). */
    private static int whichFloor(int[] floorStart, int index) {
        for (int f = 0; f < floorStart.length - 1; f++) {
            if (index >= floorStart[f] && index < floorStart[f + 1]) {
                return f;
            }
        }
        return -1;
    }
}
