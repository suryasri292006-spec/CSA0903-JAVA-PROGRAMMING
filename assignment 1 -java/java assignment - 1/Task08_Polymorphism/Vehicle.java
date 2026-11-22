abstract class Vehicle { 
    private final String regNo; 
    protected Vehicle(String regNo) { this.regNo = regNo; }
    public String getRegNo() { return regNo; } 

    protected abstract double ratePerHour(); 

    /** Every started hour is charged in full. */ 
    public double calculateFee(int minutes) { 
        // Implemented: Compute billable hours as the ceiling of minutes / 60 using integer
        // arithmetic only, and return hours x ratePerHour()
        int hours = (minutes + 59) / 60;       // integer ceiling: 130 -> 3, 120 -> 2, 0 -> 0
        return hours * ratePerHour();
    } 

    @Override public boolean equals(Object o) { 
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo); 
    } 

    @Override public int hashCode() { return regNo.hashCode(); } 
}
