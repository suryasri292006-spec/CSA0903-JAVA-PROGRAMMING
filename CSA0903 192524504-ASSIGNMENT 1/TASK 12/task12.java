
import java.time.LocalDate;
import java.time.LocalTime;

// 1. PricingRule interface
interface PricingRule {
    double calculateFee(int minutes, LocalTime exitTime);
}

// 2. Car pricing rule
class CarRule implements PricingRule {
    public double calculateFee(int minutes, LocalTime exitTime) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Invalid duration!");
        }
        if (minutes == 0) return 0;
        if (minutes <= 60) return 20;
        if (minutes <= 120) return 30;
        return 50;
    }
}

// 3. Bike pricing rule
class BikeRule implements PricingRule {
    public double calculateFee(int minutes, LocalTime exitTime) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Invalid duration!");
        }
        if (minutes == 0) return 0;
        if (minutes <= 60) return 10;
        if (minutes <= 120) return 20;
        return 30;
    }
}

// 4. Electric car pricing rule
class ElectricCarRule implements PricingRule {
    public double calculateFee(int minutes, LocalTime exitTime) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Invalid duration!");
        }
        if (minutes == 0) return 0;
        if (minutes <= 60) return 15;
        if (minutes <= 120) return 25;
        return 40;
    }
}

// 5. E-bike flat pricing rule
class EBikeRule implements PricingRule {
    public double calculateFee(int minutes, LocalTime exitTime) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Invalid duration!");
        }
        return 15;
    }
}

// 6. Delivery van pricing rule
class VanRule implements PricingRule {
    private PricingRule carRule = new CarRule();

    public double calculateFee(int minutes, LocalTime exitTime) {
        double carFee = carRule.calculateFee(minutes, exitTime);

        // Double car rate from 9 AM to 5 PM
        if (!exitTime.isBefore(LocalTime.of(9, 0))
                && exitTime.isBefore(LocalTime.of(17, 0))) {
            return carFee * 2;
        }

        return carFee;
    }
}

// 7. Abstract Vehicle class
abstract class Vehicle {
    private String registrationNumber;
    private PricingRule pricingRule;

    public Vehicle(String registrationNumber, PricingRule pricingRule) {
        if (registrationNumber == null ||
                registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid registration number!");
        }

        this.registrationNumber = registrationNumber.trim();
        this.pricingRule = pricingRule;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public double calculateFee(int minutes, LocalTime exitTime) {
        return pricingRule.calculateFee(minutes, exitTime);
    }
}

// 8. Car class
class Car extends Vehicle {
    public Car(String registration) {
        super(registration, new CarRule());
    }
}

// 9. Bike class
class Bike extends Vehicle {
    public Bike(String registration) {
        super(registration, new BikeRule());
    }
}

// 10. Electric car class
class ElectricCar extends Vehicle {
    public ElectricCar(String registration) {
        super(registration, new ElectricCarRule());
    }
}

// 11. New E-bike class
class EBike extends Vehicle {
    public EBike(String registration) {
        super(registration, new EBikeRule());
    }
}

// 12. New Delivery Van class
class DeliveryVan extends Vehicle {
    public DeliveryVan(String registration) {
        super(registration, new VanRule());
    }
}

// 13. Pass interface
interface Pass {
    double getPrice();
    boolean isValid(LocalDate date);
}

// 14. One-day pass implementation
class OneDayPass implements Pass {
    private LocalDate validDate;
    private final double price = 100;

    public OneDayPass(LocalDate validDate) {
        this.validDate = validDate;
    }

    public double getPrice() {
        return price;
    }

    public boolean isValid(LocalDate date) {
        return validDate.equals(date);
    }
}

// 15. ParkingTicket class
class ParkingTicket {
    private Vehicle vehicle;
    private int floorNumber;
    private int slotNumber;
    private Pass pass;

    public ParkingTicket(Vehicle vehicle, int floor,
                         int slot, Pass pass) {
        this.vehicle = vehicle;
        this.floorNumber = floor;
        this.slotNumber = slot;
        this.pass = pass;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Pass getPass() {
        return pass;
    }
}

// 16. ParkingLot class
class ParkingLot {
    private ParkingTicket[][] floors;
    private double totalCollection = 0;

    public ParkingLot(int[] slotCounts) {
        if (slotCounts == null || slotCounts.length != 5) {
            throw new IllegalArgumentException(
                "Parking must have five floors!");
        }

        floors = new ParkingTicket[5][];

        for (int i = 0; i < 5; i++) {
            if (slotCounts[i] <= 0) {
                throw new IllegalArgumentException(
                    "Invalid slot capacity!");
            }

            floors[i] = new ParkingTicket[slotCounts[i]];
        }
    }

    // Create vehicle
    private Vehicle createVehicle(String type, String registration) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car(registration);

            case "bike":
                return new Bike(registration);

            case "electric":
                return new ElectricCar(registration);

            case "ebike":
                return new EBike(registration);

            case "van":
                return new DeliveryVan(registration);

            default:
                throw new IllegalArgumentException(
                    "Unknown vehicle type!");
        }
    }

    // Vehicle entry
    public void vehicleEntry(String type, String registration,
                             boolean buyPass, LocalDate date) {

        Vehicle vehicle = createVehicle(type, registration);

        // Check duplicate registration
        for (int i = 0; i < floors.length; i++) {
            for (int j = 0; j < floors[i].length; j++) {
                ParkingTicket t = floors[i][j];

                if (t != null && t.getVehicle()
                        .getRegistrationNumber()
                        .equalsIgnoreCase(registration.trim())) {
                    throw new IllegalArgumentException(
                        "Duplicate registration!");
                }
            }
        }

        // Find free floor and slot
        for (int i = 0; i < floors.length; i++) {
            for (int j = 0; j < floors[i].length; j++) {

                if (floors[i][j] == null) {

                    Pass pass = null;

                    if (buyPass) {
                        pass = new OneDayPass(date);
                        totalCollection += pass.getPrice();
                    }

                    floors[i][j] = new ParkingTicket(
                        vehicle, i + 1, j + 1, pass);

                    System.out.println(registration +
                        " parked at Floor " + (i + 1) +
                        ", Slot " + (j + 1));

                    if (buyPass) {
                        System.out.println(
                            "One-day pass purchased: Rs.100");
                    }

                    return;
                }
            }
        }

        throw new IllegalStateException("Parking is full!");
    }

    // Vehicle exit
    public double vehicleExit(String registration,
                              int minutes,
                              LocalDate date,
                              LocalTime exitTime) {

        if (minutes < 0 || registration == null) {
            throw new IllegalArgumentException("Invalid input!");
        }

        for (int i = 0; i < floors.length; i++) {
            for (int j = 0; j < floors[i].length; j++) {

                ParkingTicket ticket = floors[i][j];

                if (ticket != null &&
                    ticket.getVehicle().getRegistrationNumber()
                    .equalsIgnoreCase(registration.trim())) {

                    double fee;

                    Pass pass = ticket.getPass();

                    if (pass != null && pass.isValid(date)) {
                        fee = 0;
                        System.out.println(
                            "One-day pass applied. Parking fee: Rs.0");
                    } else {
                        fee = ticket.getVehicle()
                            .calculateFee(minutes, exitTime);
                    }

                    totalCollection += fee;

                    System.out.println(registration +
                        " exited. Fee: Rs." + fee);

                    floors[i][j] = null;

                    return fee;
                }
            }
        }

        throw new IllegalArgumentException(
            "Parking ticket not found!");
    }

    public double getTotalCollection() {
        return totalCollection;
    }

    public void displayFloorCapacity() {
        System.out.println("\nFLOOR CAPACITY:");

        for (int i = 0; i < floors.length; i++) {
            int occupied = 0;

            for (int j = 0; j < floors[i].length; j++) {
                if (floors[i][j] != null) {
                    occupied++;
                }
            }

            System.out.println("Floor " + (i + 1) +
                ": " + occupied + "/" +
                floors[i].length + " occupied");
        }
    }
}

// 17. Main class
public class Main {

    static void test(String name, boolean result) {
        System.out.println(name + ": " +
            (result ? "PASS" : "FAIL"));
    }

    public static void main(String[] args) {

        System.out.println("NEW UNIVERSITY PARKING SYSTEM");
        System.out.println("--------------------------------");

        // Five floors with different slot capacities
        int[] capacities = {1, 2, 1, 2, 1};

        ParkingLot lot = new ParkingLot(capacities);

        LocalDate today = LocalDate.now();

        System.out.println("\n1. E-BIKE ENTRY TEST");

        lot.vehicleEntry("ebike", "TN01EB1111",
                         false, today);

        System.out.println("\n2. DELIVERY VAN ENTRY TEST");

        lot.vehicleEntry("van", "TN02DV2222",
                         false, today);

        System.out.println("\n3. ONE-DAY PASS TEST");

        lot.vehicleEntry("car", "TN03CA3333",
                         true, today);

        lot.displayFloorCapacity();

        System.out.println("\n4. E-BIKE EXIT TEST");

        double ebikeFee = lot.vehicleExit(
            "TN01EB1111", 120, today,
            LocalTime.of(10, 0));

        test("E-bike flat fee is Rs.15",
             ebikeFee == 15);

        System.out.println("\n5. DELIVERY VAN FEE TEST");

        double vanFee = lot.vehicleExit(
            "TN02DV2222", 60, today,
            LocalTime.of(10, 0));

        test("Van double car fee is Rs.40",
             vanFee == 40);

        System.out.println("\n6. ONE-DAY PASS EXIT TEST");

        double passFee = lot.vehicleExit(
            "TN03CA3333", 120, today,
            LocalTime.of(14, 0));

        test("One-day pass gives zero exit fee",
             passFee == 0);

        System.out.println("\n7. COLLECTION TEST");

        double expected = 100 + 15 + 40;

        System.out.println("Pass collection: Rs.100");
        System.out.println("E-bike collection: Rs." + ebikeFee);
        System.out.println("Van collection: Rs." + vanFee);

        System.out.println("Expected collection: Rs." + expected);

        System.out.println("Actual collection: Rs." +
            lot.getTotalCollection());

        test("Collection matches",
            lot.getTotalCollection() == expected);

        System.out.println("\n8. FINAL FLOOR STATUS");

        lot.displayFloorCapacity();

        System.out.println("\nALL NEW REQUIREMENT TESTS COMPLETED");
    }
}
