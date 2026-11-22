/** One class, several behaviours: inherits from ONE class but implements TWO interfaces. */
public class ElectricCar extends Car implements Chargeable, Comparable<Vehicle> {
    public ElectricCar(String regNo) { super(regNo); }

    @Override public double chargingFee(int kWh) { return kWh * RATE_PER_KWH; }

    @Override public int compareTo(Vehicle other) { return getRegNo().compareTo(other.getRegNo()); }
}
