
import java.util.Arrays;

// ParkingFacility class
class ParkingFacility {

    private final String[][] slots;

    // Constructor
    public ParkingFacility(int[] slotsPerFloor) {

        slots = new String[slotsPerFloor.length][];

        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    // Method 1: Allocate parking slot
    public int[] allocate(String vehicleNo) {

        for (int f = 0; f < slots.length; f++) {

            for (int s = 0; s < slots[f].length; s++) {

                // Skip occupied slots
                if (slots[f][s] != null) {
                    continue;
                }

                // Allocate free slot
                slots[f][s] = vehicleNo;

                return new int[]{f, s};
            }
        }

        return null;
    }

    // Method 2: Release parking slot
    public boolean release(int floor, int slot) {

        if (floor < 0 || floor >= slots.length) {
            return false;
        }

        if (slot < 0 || slot >= slots[floor].length) {
            return false;
        }

        if (slots[floor][slot] == null) {
            return false;
        }

        slots[floor][slot] = null;

        return true;
    }

    // Method 3: Calculate parking fee
    public double calculateFee(long minutes) {

        if (minutes <= 0) {
            return 0;
        }

        long hours = (minutes + 59) / 60;

        double fee;

        if (hours == 1) {
            fee = 20;
        } else {
            fee = 20 + (hours - 1) * 10;
        }

        if (fee > 200) {
            fee = 200;
        }

        return fee;
    }
}

// Main class - required by online compiler
public class Main {

    public static void main(String[] args) {

        // Floor 0 has 2 slots, floor 1 has 1 slot
        ParkingFacility p =
                new ParkingFacility(new int[]{2, 1});

        System.out.println(
                Arrays.toString(p.allocate("TN01AB1111"))
        );

        System.out.println(
                Arrays.toString(p.allocate("TN02CD2222"))
        );

        System.out.println(
                Arrays.toString(p.allocate("TN03EF3333"))
        );

        System.out.println(
                Arrays.toString(p.allocate("TN04GH4444"))
        );

        System.out.println(
                p.release(0, 1) + " "
                + p.release(0, 1) + " "
                + p.release(5, 0)
        );

        System.out.println(
                Arrays.toString(p.allocate("TN04GH4444"))
        );
    }
}
