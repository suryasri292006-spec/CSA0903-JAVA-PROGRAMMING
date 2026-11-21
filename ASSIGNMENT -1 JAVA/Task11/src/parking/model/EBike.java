package parking.model;

/** Task 12: e-bikes pay a flat fee for any stay longer than zero minutes. */
public class EBike extends Vehicle {
    private static final double FLAT_FEE = 20;
    public EBike(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return FLAT_FEE; }
    @Override public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Negative duration");
        return minutes == 0 ? 0 : FLAT_FEE;
    }
}
