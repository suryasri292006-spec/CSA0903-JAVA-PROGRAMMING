package parking.model;

import java.time.LocalDateTime;

/** A pass that can waive the PARKING fee for a vehicle. New pass kinds just implement this. */
public interface Pass {
    String getRegNo();

    /** True when the whole stay from entry to exit is covered by this pass. */
    boolean covers(LocalDateTime entry, LocalDateTime exit);

    /** Price paid to buy the pass (0 when issued outside this system). */
    double getPrice();

    String describe();
}
