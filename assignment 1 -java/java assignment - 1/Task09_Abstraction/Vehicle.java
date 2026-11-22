/** Task 9 - ABSTRACT CLASS: shared state (regNo) + shared code + one abstract "how" method. */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) { this.regNo = regNo; }   // constructors exist only in classes

    public String getRegNo() { return regNo; }

    /** The "what": every vehicle has an hourly rate. The "how" is left to the subclass. */
    protected abstract double ratePerHour();

    /** Concrete method shared by all subclasses (template for fee calculation). */
    public double calculateFee(int minutes) {
        return ((minutes + 59) / 60) * ratePerHour();
    }
}
