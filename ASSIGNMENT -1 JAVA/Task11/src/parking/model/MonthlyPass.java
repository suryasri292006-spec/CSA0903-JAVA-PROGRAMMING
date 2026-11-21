package parking.model;

import java.time.LocalDate;

/** Staff/student monthly pass: free parking between validFrom and validTo (inclusive). */
public class MonthlyPass implements Pass {
    private final LocalDate validFrom;
    private final LocalDate validTo;

    public MonthlyPass(LocalDate validFrom, LocalDate validTo) {
        if (validFrom == null || validTo == null || validTo.isBefore(validFrom))
            throw new IllegalArgumentException("Invalid pass period");
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @Override
    public double apply(double fee, LocalDate on) {
        boolean valid = !on.isBefore(validFrom) && !on.isAfter(validTo);
        return valid ? 0 : fee;
    }
}
