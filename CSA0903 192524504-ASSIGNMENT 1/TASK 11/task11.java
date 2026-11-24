
abstract class Vehicle {
    protected String registrationNumber;

    public Vehicle(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public abstract double calculateFee(int minutes);
}

class Car extends Vehicle {
    public Car(String registrationNumber) {
        super(registrationNumber);
    }

    public double calculateFee(int minutes) {
        if (minutes == 0) return 0;
        if (minutes <= 60) return 20;
        if (minutes <= 120) return 30;
        return 50;
    }
}

class Bike extends Vehicle {
    public Bike(String registrationNumber) {
        super(registrationNumber);
    }

    public double calculateFee(int minutes) {
        if (minutes == 0) return 0;
        if (minutes <= 60) return 10;
        if (minutes <= 120) return 20;
        return 30;
    }
}

class ElectricCar extends Vehicle {
    public ElectricCar(String registrationNumber) {
        super(registrationNumber);
    }

    public double calculateFee(int minutes) {
        if (minutes == 0) return 0;
        if (minutes <= 60) return 15;
        if (minutes <= 120) return 25;
        return 40;
    }
}

class ParkingTicket {
    private Vehicle vehicle;
    private int slotNumber;

    public ParkingTicket(Vehicle vehicle, int slotNumber) {
        this.vehicle = vehicle;
        this.slotNumber = slotNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}

class ParkingLot {
    private ParkingTicket[] tickets;
    private int capacity;
    private int count;
    private double totalCollection;

    public ParkingLot(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Invalid parking capacity!");
        }

        this.capacity = capacity;
        tickets = new ParkingTicket[capacity];
        count = 0;
        totalCollection = 0;
    }

    // Create vehicle based on type
    private Vehicle createVehicle(String type, String registration) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car(registration);

            case "bike":
                return new Bike(registration);

            case "electric":
                return new ElectricCar(registration);

            default:
                throw new IllegalArgumentException(
                    "Unknown vehicle type!");
        }
    }

    // Vehicle entry
    public int vehicleEntry(String type, String registration) {

        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid registration number!");
        }

        // Check duplicate registration
        for (int i = 0; i < count; i++) {
            if (tickets[i].getVehicle()
                    .getRegistrationNumber().equalsIgnoreCase(registration)) {
                throw new IllegalArgumentException(
                    "Duplicate registration number!");
            }
        }

        if (count == capacity) {
            throw new IllegalStateException(
                "Parking lot is full!");
        }

        Vehicle vehicle = createVehicle(type, registration);

        int slotNumber = -1;

        // Find the first free slot
        boolean[] occupied = new boolean[capacity];

        for (int i = 0; i < count; i++) {
            occupied[tickets[i].getSlotNumber() - 1] = true;
        }

        for (int i = 0; i < capacity; i++) {
            if (!occupied[i]) {
                slotNumber = i + 1;
                break;
            }
        }

        tickets[count] = new ParkingTicket(vehicle, slotNumber);
        count++;

        System.out.println(registration +
            " entered successfully. Slot: " + slotNumber);

        return slotNumber;
    }

    // Vehicle exit
    public double vehicleExit(String registration, int minutes) {

        if (minutes < 0) {
            throw new IllegalArgumentException(
                "Invalid parking duration!");
        }

        for (int i = 0; i < count; i++) {

            Vehicle vehicle = tickets[i].getVehicle();

            if (vehicle.getRegistrationNumber()
                    .equalsIgnoreCase(registration)) {

                double fee = vehicle.calculateFee(minutes);

                System.out.println(registration +
                    " exited. Fee: Rs." + fee);

                totalCollection += fee;

                // Remove ticket from array
                for (int j = i; j < count - 1; j++) {
                    tickets[j] = tickets[j + 1];
                }

                tickets[count - 1] = null;
                count--;

                return fee;
            }
        }

        throw new IllegalArgumentException(
            "Parking ticket not found!");
    }

    // Validate slot number
    public void validateSlot(int slotNumber) {
        if (slotNumber <= 0 || slotNumber > capacity) {
            throw new IllegalArgumentException(
                "Invalid slot number!");
        }
    }

    public double getTotalCollection() {
        return totalCollection;
    }

    public int getAvailableSlots() {
        return capacity - count;
    }

    public void displayStatus() {
        System.out.println("Occupied slots: " + count);
        System.out.println("Available slots: " +
            getAvailableSlots());
    }
}

public class Main {

    static void test(String name, boolean result) {
        System.out.println(name + ": " +
            (result ? "PASS" : "FAIL"));
    }

    public static void main(String[] args) {

        System.out.println("PARKING MANAGEMENT SYSTEM");
        System.out.println("-------------------------");

        ParkingLot lot = new ParkingLot(3);

        // Test 1: Normal vehicle entry
        System.out.println("\n1. NORMAL ENTRY TEST");

        int s1 = lot.vehicleEntry("car", "TN01AB1234");
        int s2 = lot.vehicleEntry("bike", "TN02CD5678");

        test("Car gets slot 1", s1 == 1);
        test("Bike gets slot 2", s2 == 2);

        // Test 2: Last free slot
        System.out.println("\n2. LAST FREE SLOT TEST");

        int s3 = lot.vehicleEntry("electric", "TN03EF9012");

        test("Last free slot allocated", s3 == 3);
        test("No slots available",
            lot.getAvailableSlots() == 0);

        lot.displayStatus();

        // Test 3: Duplicate registration
        System.out.println("\n3. DUPLICATE REGISTRATION TEST");

        try {
            lot.vehicleEntry("car", "TN01AB1234");
            test("Duplicate registration rejected", false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            test("Duplicate registration rejected", true);
        }

        // Test 4: Unknown vehicle type
        System.out.println("\n4. UNKNOWN VEHICLE TEST");

        try {
            lot.vehicleEntry("truck", "TN04GH1111");
            test("Unknown vehicle rejected", false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            test("Unknown vehicle rejected", true);
        }

        // Test 5: Invalid slot
        System.out.println("\n5. INVALID SLOT TEST");

        try {
            lot.validateSlot(5);
            test("Invalid slot rejected", false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            test("Invalid slot rejected", true);
        }

        // Test 6: Fee boundary and zero-minute parking
        System.out.println("\n6. FEE BOUNDARY TEST");

        double carFee = lot.vehicleExit("TN01AB1234", 60);
        double bikeFee = lot.vehicleExit("TN02CD5678", 0);
        double electricFee = lot.vehicleExit("TN03EF9012", 120);

        test("Car fee at 60 minutes is Rs.20",
            carFee == 20);

        test("Bike fee at zero minutes is Rs.0",
            bikeFee == 0);

        test("Electric car fee at 120 minutes is Rs.25",
            electricFee == 25);

        // Test 7: Daily collection
        System.out.println("\n7. DAILY COLLECTION");

        System.out.println("Car fee: Rs." + carFee);
        System.out.println("Bike fee: Rs." + bikeFee);
        System.out.println("Electric car fee: Rs." + electricFee);

        double expected = 20 + 0 + 25;

        System.out.println("Manually calculated total: Rs." +
            expected);

        System.out.println("Program collection: Rs." +
            lot.getTotalCollection());

        test("Daily collection matches",
            lot.getTotalCollection() == expected);

        // Test 8: Normal exit and free slots
        System.out.println("\n8. EXIT TEST");

        test("All slots are free",
            lot.getAvailableSlots() == 3);

        lot.displayStatus();

        // Test 9: Invalid ticket
        System.out.println("\n9. INVALID EXIT TEST");

        try {
            lot.vehicleExit("TN99ZZ9999", 30);
            test("Unknown registration rejected", false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            test("Unknown registration rejected", true);
        }

        System.out.println("\nALL TESTS COMPLETED");
    }
}
