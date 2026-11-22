/** Task 6 - subclass: adds nothing new, supplies the rate. */
public class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo) {
        super(regNo);                              // must be the first statement
        System.out.println("  [2] TwoWheeler(String) constructor ran");
    }
    @Override protected double ratePerHour() { return 10; }
}
