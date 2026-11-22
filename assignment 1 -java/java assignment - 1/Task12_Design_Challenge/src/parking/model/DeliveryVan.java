package parking.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * NEW (Task 12): delivery van = car rate, but DOUBLE the car rate for the minutes that fall
 * inside 09:00-17:00. Only the overlap of the stay with that window is surcharged,
 * so the entry time alone is never used.
 *
 *   fee = normal car fee for the whole stay  +  surcharge for the overlapping minutes
 *   surcharge = ceil(overlapMinutes x carRatePerHour / 60)   (an extra "1 x rate" = double in total)
 */
public class DeliveryVan extends Car {
    private static final LocalTime PEAK_START = LocalTime.of(9, 0);
    private static final LocalTime PEAK_END = LocalTime.of(17, 0);

    public DeliveryVan(String regNo) { super(regNo); }

    @Override
    public double calculateFee(LocalDateTime entry, LocalDateTime exit) {
        double normalFee = super.calculateFee(entry, exit);          // validates times, applies hourly slabs and cap
        long overlap = peakMinutes(entry, exit);
        long rate = (long) ratePerHour();
        long surcharge = (overlap * rate + 59) / 60;                 // integer ceiling, no floating point error
        return normalFee + surcharge;
    }

    /** Minutes of the stay that overlap 09:00-17:00 on any day the stay touches. */
    public static long peakMinutes(LocalDateTime entry, LocalDateTime exit) {
        long total = 0;
        for (LocalDate day = entry.toLocalDate(); !day.isAfter(exit.toLocalDate()); day = day.plusDays(1)) {
            LocalDateTime windowStart = day.atTime(PEAK_START);
            LocalDateTime windowEnd = day.atTime(PEAK_END);
            LocalDateTime from = entry.isAfter(windowStart) ? entry : windowStart;   // later of the two starts
            LocalDateTime to = exit.isBefore(windowEnd) ? exit : windowEnd;          // earlier of the two ends
            if (to.isAfter(from)) {
                total += Duration.between(from, to).toMinutes();
            }
        }
        return total;
    }
}
