package parking.model;

/** A car that may also use a charging point (Rs 12 per kWh, charged separately). */
public class ElectricCar extends Car implements Chargeable {
    public static final double RATE_PER_KWH = 12;

    public ElectricCar(String regNo) { super(regNo); }

    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0) {
            throw new IllegalArgumentException("Charged energy cannot be negative: " + kWh);
        }
        return kWh * RATE_PER_KWH;
    }
}
