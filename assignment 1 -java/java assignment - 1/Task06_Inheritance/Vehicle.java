/** Task 6 - superclass of the hierarchy (abstract: a plain "vehicle" is never parked). */
public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        this.regNo = regNo;
        System.out.println("  [1] Vehicle(String) constructor ran for " + regNo);
    }

    public String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    /** Inherited by all subclasses; subclasses extend it using super.describe(). */
    public String describe() {
        return "Vehicle " + regNo + " rate Rs " + ratePerHour() + "/hour";
    }
}
