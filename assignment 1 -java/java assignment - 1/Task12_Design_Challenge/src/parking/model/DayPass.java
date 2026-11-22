package parking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** NEW (Task 12): one-day pass bought by a visitor. Covers a stay that starts and ends on the pass date. */
public class DayPass implements Pass {
    public static final double PRICE = 100;

    private final String regNo;
    private final LocalDate date;

    public DayPass(String regNo, LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Pass date is required");
        }
        this.regNo = Vehicle.validateRegNo(regNo);
        this.date = date;
    }

    @Override public String getRegNo() { return regNo; }

    @Override
    public boolean covers(LocalDateTime entry, LocalDateTime exit) {
        return entry.toLocalDate().equals(date) && exit.toLocalDate().equals(date);
    }

    @Override public double getPrice() { return PRICE; }

    @Override
    public String describe() {
        return "Day pass " + regNo + " valid on " + date + " (price Rs " + PRICE + ")";
    }
}
