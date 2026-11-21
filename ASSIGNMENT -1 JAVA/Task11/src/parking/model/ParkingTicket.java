package parking.model;

import java.time.LocalDateTime;

public class ParkingTicket {
    private static int counter = 0;             // shared by all tickets: never reused

    private final int ticketNo;
    private final Vehicle vehicle;
    private final Pass pass;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;

    public ParkingTicket(Vehicle vehicle, Pass pass, int floor, int slot, LocalDateTime entryTime) {
        if (vehicle == null || pass == null || entryTime == null)
            throw new IllegalArgumentException("Null argument");
        if (floor < 0 || slot < 0) throw new IllegalArgumentException("Invalid slot position");
        this.ticketNo = ++counter;
        this.vehicle = vehicle;
        this.pass = pass;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    /** Overloaded constructor: visitor without a pass (constructor chaining). */
    public ParkingTicket(Vehicle vehicle, int floor, int slot, LocalDateTime entryTime) {
        this(vehicle, Pass.NONE, floor, slot, entryTime);
    }

    /** Copy constructor: re-issues a duplicate ticket with the SAME ticket number. */
    public ParkingTicket(ParkingTicket other) {
        this.ticketNo = other.ticketNo;
        this.vehicle = other.vehicle;
        this.pass = other.pass;
        this.floor = other.floor;
        this.slot = other.slot;
        this.entryTime = other.entryTime;
    }

    public static int issuedCount() { return counter; }
    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public Pass getPass() { return pass; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override public String toString() {
        return "Ticket#" + ticketNo + " " + vehicle + " floor " + floor + " slot " + slot;
    }
}
