/** Task 4 - ParkingTicket with overloaded constructors, chaining and a copy constructor. */
public class ParkingTicket {
    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final int entryMinute;    // minutes since midnight

    /** Full constructor - every other constructor ends up here. */
    public ParkingTicket(int ticketNo, Vehicle vehicle, int floor, int slot, int entryMinute) {
        this.ticketNo = ticketNo;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryMinute = entryMinute;
    }

    /** Overloaded: slot not yet allocated (-1, -1). */
    public ParkingTicket(int ticketNo, Vehicle vehicle, int entryMinute) {
        this(ticketNo, vehicle, -1, -1, entryMinute);
    }

    /** Default constructor: an empty placeholder ticket (ticketNo 0). */
    public ParkingTicket() {
        this(0, new Vehicle(), 0);
    }

    /** Copy constructor: used to re-issue a DUPLICATE ticket with the same number. */
    public ParkingTicket(ParkingTicket other) {
        this(other.ticketNo, new Vehicle(other.vehicle), other.floor, other.slot, other.entryMinute);
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getEntryMinute() { return entryMinute; }

    @Override
    public String toString() {
        return "Ticket#" + ticketNo + " " + vehicle + " slot=[" + floor + "," + slot + "] entry=" + entryMinute;
    }
}
