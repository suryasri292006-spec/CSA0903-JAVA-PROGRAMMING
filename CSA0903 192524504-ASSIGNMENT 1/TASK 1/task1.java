
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Class 1: Vehicle
class Vehicle {
    String registrationNumber;
    String type;
    String category;
    boolean validPass;
    boolean needsCharging;

    Vehicle(String registrationNumber, String type,
            String category, boolean validPass,
            boolean needsCharging) {

        this.registrationNumber = registrationNumber;
        this.type = type;
        this.category = category;
        this.validPass = validPass;
        this.needsCharging = needsCharging;
    }
}

// Class 2: ParkingSlot
class ParkingSlot {
    int slotNumber;
    String slotType;
    boolean occupied;

    ParkingSlot(int slotNumber, String slotType) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.occupied = false;
    }

    boolean isSuitable(Vehicle vehicle) {

        if (occupied) {
            return false;
        }

        if (vehicle.needsCharging) {
            return slotType.equals("EV-Charging");
        }

        if (vehicle.type.equals("Two-Wheeler")) {
            return slotType.equals("Two-Wheeler");
        }

        return slotType.equals("Car")
                || slotType.equals("EV-Charging");
    }
}

// Class 3: ParkingFloor
class ParkingFloor {
    int floorNumber;
    ArrayList<ParkingSlot> slots;

    ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        slots = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {

            String type;

            if (i % 10 == 0) {
                type = "EV-Charging";
            }
            else if (i % 5 == 0) {
                type = "Two-Wheeler";
            }
            else {
                type = "Car";
            }

            slots.add(new ParkingSlot(i, type));
        }
    }

    ParkingSlot findFreeSlot(Vehicle vehicle) {

        for (ParkingSlot slot : slots) {

            if (slot.isSuitable(vehicle)) {
                return slot;
            }
        }

        return null;
    }
}

// Class 4: ParkingTicket
class ParkingTicket {

    static int nextTicketNumber = 1000;

    int ticketNumber;
    Vehicle vehicle;
    int floorNumber;
    int slotNumber;
    LocalDateTime entryTime;

    ParkingTicket(Vehicle vehicle, int floorNumber,
                  int slotNumber) {

        this.ticketNumber = nextTicketNumber++;
        this.vehicle = vehicle;
        this.floorNumber = floorNumber;
        this.slotNumber = slotNumber;
        this.entryTime = LocalDateTime.now();
    }
}

// Class 5: ParkingLot
class ParkingLot {

    ArrayList<ParkingFloor> floors;
    Map<String, ParkingTicket> activeTickets;

    ParkingLot() {

        floors = new ArrayList<>();
        activeTickets = new HashMap<>();

        for (int i = 1; i <= 3; i++) {
            floors.add(new ParkingFloor(i));
        }
    }

    // Method to park vehicle
    void parkVehicle(Vehicle vehicle) {

        if (activeTickets.containsKey(
                vehicle.registrationNumber)) {

            System.out.println("Vehicle is already parked.");
            return;
        }

        for (ParkingFloor floor : floors) {

            ParkingSlot slot = floor.findFreeSlot(vehicle);

            if (slot != null) {

                slot.occupied = true;

                ParkingTicket ticket = new ParkingTicket(
                        vehicle,
                        floor.floorNumber,
                        slot.slotNumber
                );

                activeTickets.put(
                        vehicle.registrationNumber, ticket
                );

                System.out.println("\nVehicle parked successfully!");
                System.out.println("--------------------------------");

                System.out.println("Ticket Number: "
                        + ticket.ticketNumber);

                System.out.println("Floor Number: "
                        + ticket.floorNumber);

                System.out.println("Slot Number: "
                        + ticket.slotNumber);

                System.out.println("Slot Type: "
                        + slot.slotType);

                System.out.println("Entry Time: "
                        + ticket.entryTime);

                System.out.println("--------------------------------");

                return;
            }
        }

        System.out.println("No suitable parking slot available.");
    }

    // Method to exit vehicle
    void exitVehicle(String registrationNumber) {

        ParkingTicket ticket =
                activeTickets.get(registrationNumber);

        if (ticket == null) {

            System.out.println("No active parking ticket found.");
            return;
        }

        LocalDateTime exitTime = LocalDateTime.now();

        long minutes = Duration.between(
                ticket.entryTime, exitTime
        ).toMinutes();

        long hours = Math.max(1, (minutes + 59) / 60);

        double parkingFee = 0;

        switch (ticket.vehicle.type) {

            case "Two-Wheeler":
                parkingFee = hours * 10;
                break;

            case "Car":
                parkingFee = hours * 20;
                break;

            case "EV":
                parkingFee = hours * 25;
                break;

            default:
                System.out.println("Invalid vehicle type.");
                return;
        }

        // Monthly pass discount
        if (ticket.vehicle.validPass
                && (ticket.vehicle.category.equals("Student")
                || ticket.vehicle.category.equals("Staff"))) {

            parkingFee = 0;
        }

        // EV charging fee
        double chargingFee = 0;

        if (ticket.vehicle.needsCharging) {
            chargingFee = hours * 15;
        }

        double totalFee = parkingFee + chargingFee;

        // Release parking slot
        for (ParkingFloor floor : floors) {

            if (floor.floorNumber == ticket.floorNumber) {

                for (ParkingSlot slot : floor.slots) {

                    if (slot.slotNumber == ticket.slotNumber) {

                        slot.occupied = false;
                        break;
                    }
                }
            }
        }

        // Remove active ticket
        activeTickets.remove(registrationNumber);

        // Generate receipt
        System.out.println("\n========== PARKING RECEIPT ==========");

        System.out.println("Ticket Number: "
                + ticket.ticketNumber);

        System.out.println("Registration Number: "
                + registrationNumber);

        System.out.println("Vehicle Type: "
                + ticket.vehicle.type);

        System.out.println("Category: "
                + ticket.vehicle.category);

        System.out.println("Entry Time: "
                + ticket.entryTime);

        System.out.println("Exit Time: "
                + exitTime);

        System.out.println("Parking Duration: "
                + hours + " hour(s)");

        System.out.println("Parking Fee: Rs. "
                + parkingFee);

        System.out.println("Charging Fee: Rs. "
                + chargingFee);

        System.out.println("Total Fee: Rs. "
                + totalFee);

        System.out.println("Parking Slot Released!");

        System.out.println("=====================================");
    }
}

// Class 6: Main Class
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ParkingLot parkingLot = new ParkingLot();

        System.out.println("====================================");
        System.out.println("      CAMPUS SMART PARKING SYSTEM");
        System.out.println("====================================");

        // Registration number
        System.out.print("Enter Registration Number: ");

        String regNo = sc.nextLine().trim().toUpperCase();

        if (regNo.isEmpty()) {
            System.out.println("Registration number cannot be empty.");
            sc.close();
            return;
        }

        // Vehicle type
        System.out.println("\nSelect Vehicle Type:");
        System.out.println("1. Two-Wheeler");
        System.out.println("2. Car");
        System.out.println("3. EV");

        System.out.print("Enter your choice: ");

        String typeChoice = sc.nextLine().trim();

        String type;

        switch (typeChoice) {

            case "1":
                type = "Two-Wheeler";
                break;

            case "2":
                type = "Car";
                break;

            case "3":
                type = "EV";
                break;

            default:
                System.out.println("Invalid vehicle type!");
                sc.close();
                return;
        }

        // Category
        System.out.println("\nSelect Category:");
        System.out.println("1. Student");
        System.out.println("2. Staff");
        System.out.println("3. Visitor");

        System.out.print("Enter your choice: ");

        String categoryChoice = sc.nextLine().trim();

        String category;

        switch (categoryChoice) {

            case "1":
                category = "Student";
                break;

            case "2":
                category = "Staff";
                break;

            case "3":
                category = "Visitor";
                break;

            default:
                System.out.println("Invalid category!");
                sc.close();
                return;
        }

        // Monthly pass
        System.out.print("\nDo you have a valid monthly pass? (yes/no): ");

        String passInput = sc.nextLine().trim();

        boolean pass;

        if (passInput.equalsIgnoreCase("yes")) {
            pass = true;
        }
        else if (passInput.equalsIgnoreCase("no")) {
            pass = false;
        }
        else {
            System.out.println("Invalid input!");
            sc.close();
            return;
        }

        // EV charging
        boolean charging = false;

        if (type.equals("EV")) {

            System.out.print("Do you need EV charging? (yes/no): ");

            String chargingInput = sc.nextLine().trim();

            if (chargingInput.equalsIgnoreCase("yes")) {
                charging = true;
            }
            else if (chargingInput.equalsIgnoreCase("no")) {
                charging = false;
            }
            else {
                System.out.println("Invalid input!");
                sc.close();
                return;
            }
        }

        // Create vehicle object
        Vehicle vehicle = new Vehicle(
                regNo,
                type,
                category,
                pass,
                charging
        );

        // Park vehicle
        parkingLot.parkVehicle(vehicle);

        // Exit vehicle
        if (parkingLot.activeTickets.containsKey(regNo)) {

            System.out.println("\nPress Enter to exit the vehicle...");

            sc.nextLine();

            parkingLot.exitVehicle(regNo);
        }

        sc.close();
    }
}
