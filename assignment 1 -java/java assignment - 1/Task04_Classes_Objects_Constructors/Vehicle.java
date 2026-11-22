/** Task 4 - a simple Vehicle with default, parameterised, overloaded and copy constructors. */
public class Vehicle {
    private String regNo;
    private String type;          // TWOWHEELER, CAR or EV

    /** Default constructor: an unknown car. Used only when data is not yet known. */
    public Vehicle() {
        this("UNKNOWN", "CAR");   // constructor chaining: reuse the full constructor
    }

    /** Overloaded constructor: type defaults to CAR. */
    public Vehicle(String regNo) {
        this(regNo, "CAR");       // chaining again, no repeated code
    }

    /** Main parameterised constructor: the only place where fields are assigned. */
    public Vehicle(String regNo, String type) {
        this.regNo = regNo;       // this.regNo is the field, regNo is the parameter
        this.type = type;
    }

    /** Copy constructor: builds an independent copy (deep copy of the Strings' values). */
    public Vehicle(Vehicle other) {
        this(other.regNo, other.type);
    }

    public String getRegNo() { return regNo; }
    public String getType() { return type; }

    @Override
    public String toString() { return type + "[" + regNo + "]"; }
}
