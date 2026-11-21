package parking.model;

import java.time.LocalDate;

/** Visitor one-day pass: free parking on the single date it was bought for. */
public class DayPass implements Pass {
    private final LocalDate day;

    public DayPass(LocalDate day) {
        if (day == null) throw new IllegalArgumentException("Date required");
        this.day = day;
    }

    @Override
    public double apply(double fee, LocalDate on) {
        return on.equals(day) ? 0 : fee;
    }
}
