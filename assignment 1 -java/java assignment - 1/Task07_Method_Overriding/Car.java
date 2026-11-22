/** Task 7 - overrides calculateFee to add a daily maximum (specialised behaviour). */
public class Car extends Vehicle {
    private static final double DAILY_MAX = 240;

    public Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30; }

    @Override                                       // compiler checks: same name, parameters, return type
    public double calculateFee(int minutes) {
        double fee = super.calculateFee(minutes);   // reuse the parent's rule
        return Math.min(fee, DAILY_MAX);            // then apply the Car-specific cap
    }
}
