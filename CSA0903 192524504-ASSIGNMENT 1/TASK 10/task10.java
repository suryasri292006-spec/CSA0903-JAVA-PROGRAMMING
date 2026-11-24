
class ParkingSlot {

    // Private data members
    private Integer slotNumber;
    private Integer duration;

    // Constructor
    public ParkingSlot(Integer slotNumber, Integer duration) {
        setSlotNumber(slotNumber);
        setDuration(duration);
    }

    // Getter for slot number
    public Integer getSlotNumber() {
        return slotNumber;
    }

    // Setter with validation
    public void setSlotNumber(Integer slotNumber) {
        if (slotNumber == null || slotNumber <= 0) {
            throw new IllegalArgumentException(
                "Invalid slot number!");
        }
        this.slotNumber = slotNumber;
    }

    // Getter for duration
    public Integer getDuration() {
        return duration;
    }

    // Setter with validation
    public void setDuration(Integer duration) {
        if (duration == null || duration < 0) {
            throw new IllegalArgumentException(
                "Duration cannot be negative!");
        }
        this.duration = duration;
    }

    // Display parking details
    public void displayDetails() {
        System.out.println("Slot Number: " + slotNumber);
        System.out.println("Duration: " + duration + " hours");
    }
}

class ParkingService {

    // Update parking duration
    public void updateDuration(ParkingSlot slot, Integer duration) {
        slot.setDuration(duration);
        System.out.println("Duration updated successfully.");
    }

    // Display parking information
    public void displaySlot(ParkingSlot slot) {
        slot.displayDetails();
    }
}

public class Main {

    public static void main(String[] args) {

        // Create parking slot object
        ParkingSlot slot = new ParkingSlot(101, 3);

        // Create service object
        ParkingService service = new ParkingService();

        System.out.println("Initial Parking Details:");
        service.displaySlot(slot);

        // Update duration
        System.out.println("\nUpdating Duration:");
        service.updateDuration(slot, 5);

        System.out.println("\nUpdated Parking Details:");
        service.displaySlot(slot);

        // Test invalid duration
        System.out.println("\nTesting Invalid Data:");

        try {
            slot.setDuration(-2);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Test invalid slot number
        try {
            slot.setSlotNumber(0);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Demonstrate wrapper class and autoboxing
        Integer a = 100;
        Integer b = 100;

        System.out.println("\nWrapper Class Example:");
        System.out.println("Using equals(): " + a.equals(b));
    }
}
