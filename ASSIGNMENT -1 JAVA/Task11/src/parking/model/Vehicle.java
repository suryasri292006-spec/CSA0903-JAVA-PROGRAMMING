package parking.model;

import java.time.Duration;
import java.time.LocalTime;

public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Registration number required");
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    /** Every started hour is charged in full (integer ceiling). 0 minutes = Rs 0. */
    public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Negative duration");
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    /** Overload: fee from entry and exit time of day (stay shorter than 24 hours). */
    public double calculateFee(LocalTime in, LocalTime out) {
        return calculateFee(minutesBetween(in, out));
    }

    protected static int minutesBetween(LocalTime in, LocalTime out) {
        long m = Duration.between(in, out).toMinutes();
        if (m < 0) m += 24 * 60;           // passed midnight
        return (int) m;
    }

    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }
    @Override public int hashCode() { return regNo.hashCode(); }
    @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
}
