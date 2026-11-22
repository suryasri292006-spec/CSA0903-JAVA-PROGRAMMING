/** Task 7 - base class showing final members and Object-method overrides. */
public abstract class Vehicle {
    public static final int GRACE_MINUTES = 0;     // final constant: cannot be reassigned
    private final String regNo;                    // final field: set once in the constructor

    protected Vehicle(String regNo) { this.regNo = regNo; }

    /** final method: no subclass may change how the registration number is read. */
    public final String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    /** Default fee; subclasses may override it. Every started hour is charged. */
    public double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    // ---- Object-class methods: two vehicles are the same if the registration numbers match ----
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        return regNo.equals(((Vehicle) o).regNo);
    }

    @Override public int hashCode() { return regNo.hashCode(); }   // consistent with equals

    @Override public String toString() {
        return getClass().getSimpleName() + "[" + regNo + ", Rs " + ratePerHour() + "/h]";
    }
}
