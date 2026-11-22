/** Task 7 - uses the inherited fee rule; only the rate differs. */
public class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 10; }
}
