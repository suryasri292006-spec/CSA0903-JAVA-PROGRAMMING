package parking.model;

/** Capability of vehicles that can use an EV charging point (charged separately from parking). */
public interface Chargeable {
    double chargingFee(int kWh);
}
