package parking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import parking.model.Chargeable;
import parking.model.ParkingTicket;
import parking.model.Pass;
import parking.model.Receipt;
import parking.model.UserType;
import parking.model.Vehicle;

/** Coordinates entry, exit, slots, tickets, passes and the money collected. */
public class ParkingSystem {
    private final ParkingFacility facility;
    private final VehicleFactory factory;
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();   // key = registration number
    private final Map<String, List<Pass>> passes = new HashMap<>();

    private double parkingFeesCollected;
    private double chargingFeesCollected;
    private double passSalesCollected;

    public ParkingSystem(ParkingFacility facility, VehicleFactory factory) {
        this.facility = facility;
        this.factory = factory;
    }

    /** Vehicle enters: validate, reject duplicates, allocate a slot, issue a ticket. */
    public ParkingTicket enter(String regNo, String category, UserType userType, LocalDateTime time) {
        Vehicle vehicle = factory.create(category, regNo);          // validates category and reg number
        if (userType == null || time == null) {
            throw new IllegalArgumentException("User type and entry time are required");
        }
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Duplicate registration number: " + vehicle.getRegNo() + " is already parked");
        }
        int[] position = facility.allocate(vehicle.getRegNo());
        if (position == null) {
            throw new IllegalStateException("Parking is full: no free slot");
        }
        ParkingTicket ticket = new ParkingTicket(vehicle, userType, position[0], position[1], time);
        activeTickets.put(vehicle.getRegNo(), ticket);
        return ticket;
    }

    public Receipt exit(String regNo, LocalDateTime time) {
        return exit(regNo, time, 0);
    }

    /** Vehicle leaves: compute the fee, release the slot, record the money. */
    public Receipt exit(String regNo, LocalDateTime time, int kWh) {
        String key = Vehicle.validateRegNo(regNo);
        ParkingTicket ticket = activeTickets.get(key);
        if (ticket == null) {
            throw new IllegalArgumentException("Vehicle is not parked: " + key);
        }
        if (time == null || time.isBefore(ticket.getEntryTime())) {
            throw new IllegalArgumentException("Exit time is before entry time");
        }
        if (kWh < 0) {
            throw new IllegalArgumentException("Charged energy cannot be negative: " + kWh);
        }
        Vehicle vehicle = ticket.getVehicle();
        if (kWh > 0 && !(vehicle instanceof Chargeable)) {
            throw new IllegalArgumentException(vehicle.getCategory() + " cannot use a charging point");
        }

        double charging = (vehicle instanceof Chargeable c) ? c.chargingFee(kWh) : 0;
        boolean covered = hasCoveringPass(key, ticket.getEntryTime(), time);
        double parking = covered ? 0 : vehicle.calculateFee(ticket.getEntryTime(), time);
        long minutes = java.time.Duration.between(ticket.getEntryTime(), time).toMinutes();

        facility.release(ticket.getFloor(), ticket.getSlot());      // slot becomes free again
        activeTickets.remove(key);
        parkingFeesCollected += parking;
        chargingFeesCollected += charging;
        return new Receipt(ticket.getTicketNo(), key, vehicle.getCategory(), ticket.getFloor(), ticket.getSlot(),
                minutes, parking, charging, covered);
    }

    /** Registers any kind of pass (monthly, day, ...) and records its sale price. */
    public void registerPass(Pass pass) {
        passes.computeIfAbsent(pass.getRegNo(), k -> new ArrayList<>()).add(pass);
        passSalesCollected += pass.getPrice();
    }

    private boolean hasCoveringPass(String regNo, LocalDateTime entry, LocalDateTime exit) {
        for (Pass p : passes.getOrDefault(regNo, List.of())) {
            if (p.covers(entry, exit)) {
                return true;
            }
        }
        return false;
    }

    /** Parking availability display, one line per floor. */
    public String availabilityReport() {
        StringBuilder sb = new StringBuilder();
        for (int f = 0; f < facility.getFloorCount(); f++) {
            sb.append(String.format("Floor %d: %d/%d free%n", f, facility.freeOnFloor(f), facility.getCapacity(f)));
        }
        sb.append("Total: ").append(facility.totalFree()).append("/").append(facility.totalCapacity()).append(" free");
        return sb.toString();
    }

    public int getActiveCount() { return activeTickets.size(); }
    public double getParkingFeesCollected() { return parkingFeesCollected; }
    public double getChargingFeesCollected() { return chargingFeesCollected; }
    public double getPassSalesCollected() { return passSalesCollected; }

    public double getTotalCollection() {
        return parkingFeesCollected + chargingFeesCollected + passSalesCollected;
    }
}
