
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.Duration;
import java.math.BigDecimal;

class ParkingSlot {
    int slotNumber;
    String vehicleNumber;
    LocalDateTime entryTime;
    long durationMinutes;
    BigDecimal fee;
    boolean occupied;

    ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.vehicleNumber = "";
        this.occupied = false;
        this.fee = BigDecimal.ZERO;
    }
}

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Jagged array: different slots on each floor
        ParkingSlot[][] floors = new ParkingSlot[3][];

        int[] slotCounts = {5, 4, 3};

        // Initialize parking floors and slots
        for (int i = 0; i < floors.length; i++) {

            floors[i] = new ParkingSlot[slotCounts[i]];

            for (int j = 0; j < floors[i].length; j++) {
                floors[i][j] = new ParkingSlot(j + 1);
            }
        }

        System.out.println("================================");
        System.out.println(" CAMPUS SMART PARKING SYSTEM");
        System.out.println("================================");

        System.out.print("Enter Vehicle Number: ");
        String vehicleNumber = sc.nextLine().trim();

        if (vehicleNumber.isEmpty()) {
            System.out.println("Invalid vehicle number.");
            sc.close();
            return;
        }

        System.out.print("Enter Parking Duration (minutes): ");
        long minutes = sc.nextLong();

        if (minutes <= 0) {
            System.out.println("Invalid parking duration.");
            sc.close();
            return;
        }

        // Capture entry time
        LocalDateTime entryTime = LocalDateTime.now();

        // Convert minutes into billable hours
        long hours = (minutes + 59) / 60;

        // Parking charge per hour
        BigDecimal rate = new BigDecimal("20.00");

        BigDecimal fee = rate.multiply(
                BigDecimal.valueOf(hours)
        );

        boolean parked = false;

        // Find and allocate an available slot
        for (int i = 0; i < floors.length; i++) {

            for (int j = 0; j < floors[i].length; j++) {

                ParkingSlot slot = floors[i][j];

                if (!slot.occupied) {

                    slot.occupied = true;
                    slot.vehicleNumber = vehicleNumber;
                    slot.entryTime = entryTime;
                    slot.durationMinutes = minutes;
                    slot.fee = fee;

                    System.out.println("\nVehicle parked successfully!");

                    System.out.println("Floor Number: " + (i + 1));
                    System.out.println("Slot Number: " + slot.slotNumber);
                    System.out.println("Vehicle Number: "
                            + slot.vehicleNumber);

                    System.out.println("Entry Time: " + slot.entryTime);

                    System.out.println("Duration: "
                            + slot.durationMinutes + " minutes");

                    System.out.println("Billable Hours: " + hours);

                    System.out.println("Parking Fee: Rs. "
                            + slot.fee.toPlainString());

                    parked = true;
                    break;
                }
            }

            if (parked) {
                break;
            }
        }

        if (!parked) {
            System.out.println("No parking slot available.");
        }

        // Display array information
        System.out.println("\n----- FLOOR DETAILS -----");

        for (int i = 0; i < floors.length; i++) {

            System.out.println("Floor " + (i + 1)
                    + " has " + floors[i].length + " slots.");
        }

        sc.close();
    }
}
