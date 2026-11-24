
import java.time.LocalDateTime;
import java.time.Duration;

// Class 1: Vehicle
class Vehicle {

    String registrationNumber;
    String type;
    String category;
    boolean validPass;
    boolean needsCharging;

    // Default constructor
    Vehicle() {
        this("Not Assigned", "Car",
             "Visitor", false, false);
    }

    // Overloaded constructor
    Vehicle(String registrationNumber) {
        this(registrationNumber, "Car",
             "Visitor", false, false);
    }

    // Parameterized constructor
    Vehicle(String registrationNumber,
            String type,
            String category,
            boolean validPass,
            boolean needsCharging) {

        this.registrationNumber = registrationNumber;
        this.type = type;
        this.category = category;
        this.validPass = validPass;
        this.needsCharging = needsCharging;
    }

    void displayVehicleDetails() {
        System.out.println("Vehicle Number: "
                + registrationNumber);
        System.out.println("Vehicle Type: " + type);
        System.out.println("Category: " + category);
    }
}

// Class 2: ParkingSlot
class ParkingSlot {

    int slotNumber;
    String slotType;
    boolean occupied;

    // Default constructor
    ParkingSlot() {
        this(0, "Car");
    }

    // Parameterized constructor
    ParkingSlot(int slotNumber, String slotType) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.occupied = false;
    }

    boolean isAvailable() {
        return !occupied;
    }

    void allocate() {
        occupied = true;
    }

    void release() {
        occupied = false;
    }
}

// Class 3: ParkingFloor
class ParkingFloor {

    int floorNumber;
    ParkingSlot[] slots;

    // Parameterized constructor
    ParkingFloor(int floorNumber, int numberOfSlots) {

        this.floorNumber = floorNumber;
        this.slots = new ParkingSlot[numberOfSlots];

        for (int i = 0; i < numberOfSlots; i++) {
            slots[i] = new ParkingSlot(i + 1, "Car");
        }
    }

    ParkingSlot findFreeSlot() {

        for (ParkingSlot slot : slots) {
            if (slot.isAvailable()) {
                return slot;
            }
        }

        return null;
    }

    void displaySlots() {
        System.out.println("Floor " + floorNumber
                + " has " + slots.length + " slots.");
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

    // Default constructor
    ParkingTicket() {
        this(new Vehicle(), 1, 1);
    }

    // Parameterized constructor
    ParkingTicket(Vehicle vehicle,
                  int floorNumber,
                  int slotNumber) {

        this.ticketNumber = nextTicketNumber++;
        this.vehicle = vehicle;
        this.floorNumber = floorNumber;
        this.slotNumber = slotNumber;
        this.entryTime = LocalDateTime.now();
    }

    // Copy constructor
    ParkingTicket(ParkingTicket oldTicket) {

        this.ticketNumber = oldTicket.ticketNumber;
        this.vehicle = oldTicket.vehicle;
        this.floorNumber = oldTicket.floorNumber;
        this.slotNumber = oldTicket.slotNumber;
        this.entryTime = oldTicket.entryTime;
    }

    long calculateDuration() {
        return Duration.between(
                entryTime, LocalDateTime.now()
        ).toMinutes();
    }

    void displayTicket() {

        System.out.println("\n------ PARKING TICKET ------");
        System.out.println("Ticket Number: " + ticketNumber);
        System.out.println("Vehicle Number: "
                + vehicle.registrationNumber);
        System.out.println("Floor Number: " + floorNumber);
        System.out.println("Slot Number: " + slotNumber);
        System.out.println("Entry Time: " + entryTime);
        System.out.println("----------------------------");
    }
}

// Class 5: ParkingLot
class ParkingLot {

    ParkingFloor[] floors;

    // Parameterized constructor
    ParkingLot(int[] slotCounts) {

        floors = new ParkingFloor[slotCounts.length];

        for (int i = 0; i < slotCounts.length; i++) {
            floors[i] = new ParkingFloor(
                    i + 1, slotCounts[i]
            );
        }
    }

    ParkingTicket parkVehicle(Vehicle vehicle) {

        for (ParkingFloor floor : floors) {

            ParkingSlot slot = floor.findFreeSlot();

            if (slot != null) {

                slot.allocate();

                ParkingTicket ticket = new ParkingTicket(
                        vehicle,
                        floor.floorNumber,
                        slot.slotNumber
                );

                System.out.println(
                        "\nVehicle parked successfully!");

                return ticket;
            }
        }

        System.out.println("No parking slot available.");
        return null;
    }

    double calculateFee(ParkingTicket ticket) {

        long minutes = ticket.calculateDuration();

        long hours = Math.max(1, (minutes + 59) / 60);

        double fee = hours * 20;

        if (ticket.vehicle.validPass
                && (ticket.vehicle.category.equals("Student")
                || ticket.vehicle.category.equals("Staff"))) {
            fee = 0;
        }

        return fee;
    }

    void exitVehicle(ParkingTicket ticket) {

        if (ticket == null) {
            return;
        }

        for (ParkingFloor floor : floors) {

            if (floor.floorNumber == ticket.floorNumber) {

                for (ParkingSlot slot : floor.slots) {

                    if (slot.slotNumber == ticket.slotNumber) {
                        slot.release();
                        System.out.println(
                                "\nParking slot released.");
                        return;
                    }
                }
            }
        }
    }
}

// Main Class
public class Main {

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println(" CAMPUS SMART PARKING SYSTEM");
        System.out.println("================================");

        // Create parking lot object
        ParkingLot lot =
                new ParkingLot(new int[]{3, 2, 2});

        // Create vehicle using parameterized constructor
        Vehicle vehicle = new Vehicle(
                "TN67AB1234",
                "Car",
                "Student",
                true,
                false
        );

        // Display vehicle details
        vehicle.displayVehicleDetails();

        // Allocate parking slot and generate ticket
        ParkingTicket ticket = lot.parkVehicle(vehicle);

        if (ticket != null) {

            // Display original ticket
            ticket.displayTicket();

            // Create duplicate ticket using copy constructor
            ParkingTicket duplicate =
                    new ParkingTicket(ticket);

            System.out.println("\nDuplicate Ticket Reissued");
            duplicate.displayTicket();

            // Calculate parking fee
            double fee = lot.calculateFee(ticket);

            System.out.println("Parking Fee: Rs. " + fee);

            // Release parking slot
            lot.exitVehicle(ticket);
        }

        // Display floor information
        System.out.println("\n----- FLOOR DETAILS -----");

        for (ParkingFloor floor : lot.floors) {
            floor.displaySlots();
        }

        System.out.println("\nProgram completed successfully.");
    }
}
