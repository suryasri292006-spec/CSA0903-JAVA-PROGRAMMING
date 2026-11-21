package parking.model;

import java.time.LocalDate;

/** A pass decides how much of the parking fee is payable on a given date. */
@FunctionalInterface
public interface Pass {
    Pass NONE = (fee, on) -> fee;          // no pass: full fee
    double apply(double fee, LocalDate on);
}
