/** Task 9 - INTERFACE: a capability that unrelated classes may offer (EVs, charging kiosks ...). */
public interface Chargeable {

    double RATE_PER_KWH = 12;                  // interface fields are public static final

    double chargingFee(int kWh);               // abstract by default

    /** default method: gives every implementer a ready-made behaviour. */
    default double chargingFeeWithServiceCharge(int kWh) {
        return chargingFee(kWh) + 5;           // flat Rs 5 service charge
    }

    /** static method: utility that belongs to the interface itself. */
    static boolean isValidKwh(int kWh) {
        return kWh >= 0 && kWh <= 200;
    }
}
