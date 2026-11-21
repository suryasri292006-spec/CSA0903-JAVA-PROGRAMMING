package parking.service;

import parking.model.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingService {
    private final ParkingFacility facility;
    private final Map<String, ParkingTicket> active = new HashMap<>();  // O(1) lookup on exit
    private double totalCollection = 0;

    public ParkingService(int[] slotsPerFloor) {
        this.facility = new ParkingFacility(slotsPerFloor);
    }

    public ParkingTicket enter(Vehicle v, Pass pass, LocalDateTime time) {
        if (active.containsKey(v.getRegNo()))
            throw new IllegalArgumentException("Duplicate registration: " + v.getRegNo());
        int[] pos = facility.allocate(v.getRegNo());
        if (pos == null) throw new IllegalStateException("Facility is full");
        ParkingTicket t = new ParkingTicket(v, pass, pos[0], pos[1], time);
        active.put(v.getRegNo(), t);
        return t;
    }

    /** Releases the slot and returns the fee (parking after pass + charging). */
    public double exit(String regNo, LocalDateTime time, int kWh) {
        ParkingTicket t = active.get(regNo.trim().toUpperCase());
        if (t == null) throw new IllegalArgumentException("No active ticket for " + regNo);
        if (time.isBefore(t.getEntryTime()))
            throw new IllegalArgumentException("Exit before entry");
        Vehicle v = t.getVehicle();
        double parking = v.calculateFee(t.getEntryTime().toLocalTime(), time.toLocalTime());
        double fee = t.getPass().apply(parking, time.toLocalDate());
        if (v instanceof Chargeable c && kWh > 0) fee += c.chargingFee(kWh);
        facility.release(t.getFloor(), t.getSlot());
        active.remove(t.getVehicle().getRegNo());
        totalCollection += fee;
        return fee;
    }

    public double exit(String regNo, LocalDateTime time) { return exit(regNo, time, 0); }
    public double getTotalCollection() { return totalCollection; }
    public int activeCount() { return active.size(); }
}
