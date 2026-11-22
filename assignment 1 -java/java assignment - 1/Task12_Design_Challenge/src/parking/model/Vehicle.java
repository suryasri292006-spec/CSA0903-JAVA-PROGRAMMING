package parking.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Abstract base of every vehicle category.
 * Fee rule (first-hour / additional-hour / daily-maximum):
 *   every started hour is charged at ratePerHour(), and one 24-hour block never costs
 *   more than DAILY_CAP_HOURS x ratePerHour().
 * Subclasses supply the rate and may override the fee methods for special pricing.
 */
public abstract class Vehicle {
    private static final Pattern REG_PATTERN = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{1,3}[0-9]{1,4}");
    protected static final int DAILY_CAP_HOURS = 8;
    private static final long MINUTES_PER_DAY = 24 * 60;

    private final String regNo;

    protected Vehicle(String regNo) {
        this.regNo = validateRegNo(regNo);
    }

    /** Trims, upper-cases and validates a registration number such as TN01AB1111. */
    public static String validateRegNo(String regNo) {
        if (regNo == null) {
            throw new IllegalArgumentException("Registration number is missing");
        }
        String normal = regNo.trim().toUpperCase();
        if (!REG_PATTERN.matcher(normal).matches()) {
            throw new IllegalArgumentException("Invalid registration number: " + regNo);
        }
        return normal;
    }

    public final String getRegNo() {
        return regNo;
    }

    /** Hourly rate in rupees. */
    protected abstract double ratePerHour();

    public String getCategory() {
        return getClass().getSimpleName();
    }

    /** Fee for a stay of the given length. Integer arithmetic only for the hour count. */
    public double calculateFee(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        }
        long fullDays = minutes / MINUTES_PER_DAY;
        long remainder = minutes % MINUTES_PER_DAY;
        long billableHours = (remainder + 59) / 60;              // integer ceiling
        double dailyCap = ratePerHour() * DAILY_CAP_HOURS;
        return fullDays * dailyCap + Math.min(billableHours * ratePerHour(), dailyCap);
    }

    /** Overload: fee between two moments. Rules that depend on the clock override this one. */
    public double calculateFee(LocalDateTime entry, LocalDateTime exit) {
        if (entry == null || exit == null) {
            throw new IllegalArgumentException("Entry and exit time are required");
        }
        if (exit.isBefore(entry)) {
            throw new IllegalArgumentException("Exit time is before entry time");
        }
        return calculateFee(Duration.between(entry, exit).toMinutes());
    }

    // Two vehicles are the same vehicle when the registration number matches.
    @Override
    public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }

    @Override
    public String toString() {
        return getCategory() + "[" + regNo + "]";
    }
}
