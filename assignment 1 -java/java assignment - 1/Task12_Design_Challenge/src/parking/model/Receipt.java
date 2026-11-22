package parking.model;

/** Result of a vehicle leaving: what was charged and why. */
public record Receipt(String ticketNo, String regNo, String category, int floor, int slot,
                      long minutes, double parkingFee, double chargingFee, boolean passApplied) {

    public double total() {
        return parkingFee + chargingFee;
    }
}
