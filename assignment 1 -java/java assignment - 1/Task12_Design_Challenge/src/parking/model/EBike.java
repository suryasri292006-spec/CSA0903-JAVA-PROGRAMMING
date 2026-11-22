package parking.model;


/** NEW (Task 12): e-bike pays one flat fee per visit, whatever the duration. */
public class EBike extends Vehicle {
    public static final double FLAT_FEE = 20;

    public EBike(String regNo) { super(regNo); }

    /** Flat-fee category: there is no hourly rate. */
    @Override
    protected double ratePerHour() { return 0; }

    /** Overrides the duration-based rule: every stay costs the flat fee. */
    @Override
    public double calculateFee(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        }
        return FLAT_FEE;
    }
}
