package parking.model;

import java.time.LocalDateTime;

/** Issued at entry. Immutable: once printed, a ticket never changes. */
public class ParkingTicket {
    private static int counter = 0;                     // shared by every ticket: numbers never repeat

    private final String ticketNo;
    private final Vehicle vehicle;
    private final UserType userType;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;

    public ParkingTicket(Vehicle vehicle, UserType userType, int floor, int slot, LocalDateTime entryTime) {
        if (vehicle == null || userType == null || entryTime == null) {
            throw new IllegalArgumentException("Vehicle, user type and entry time are required");
        }
        this.ticketNo = String.format("T%04d", ++counter);
        this.vehicle = vehicle;
        this.userType = userType;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    public String getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public UserType getUserType() { return userType; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override
    public String toString() {
        return ticketNo + " " + vehicle + " " + userType + " floor " + floor + " slot " + slot + " at " + entryTime;
    }
}
