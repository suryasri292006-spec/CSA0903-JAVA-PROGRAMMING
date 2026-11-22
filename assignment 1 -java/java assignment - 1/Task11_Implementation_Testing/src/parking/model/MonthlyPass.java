package parking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Monthly pass for students and staff: free parking between two dates (inclusive). */
public class MonthlyPass implements Pass {
    private final String regNo;
    private final UserType owner;
    private final LocalDate validFrom;
    private final LocalDate validTo;

    public MonthlyPass(String regNo, UserType owner, LocalDate validFrom, LocalDate validTo) {
        if (owner == null || owner == UserType.VISITOR) {
            throw new IllegalArgumentException("Monthly pass is only for students and staff");
        }
        if (validFrom == null || validTo == null || validTo.isBefore(validFrom)) {
            throw new IllegalArgumentException("Invalid pass validity dates");
        }
        this.regNo = Vehicle.validateRegNo(regNo);
        this.owner = owner;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @Override public String getRegNo() { return regNo; }

    @Override
    public boolean covers(LocalDateTime entry, LocalDateTime exit) {
        return !entry.toLocalDate().isBefore(validFrom) && !exit.toLocalDate().isAfter(validTo);
    }

    @Override public double getPrice() { return 0; }

    @Override
    public String describe() {
        return "Monthly pass (" + owner + ") " + regNo + " valid " + validFrom + " to " + validTo;
    }
}
