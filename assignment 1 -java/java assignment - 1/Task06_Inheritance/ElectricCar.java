/** Task 6 - grandchild of Vehicle: adds a new field (battery) and keeps everything from Car. */
public class ElectricCar extends Car {
    private final int batteryKWh;                // added by the subclass

    public ElectricCar(String regNo) {
        this(regNo, 40);                         // chaining inside the same class
        System.out.println("  [4] ElectricCar(String) constructor ran");
    }

    public ElectricCar(String regNo, int batteryKWh) {
        super(regNo);                            // goes up to Car, which goes up to Vehicle
        this.batteryKWh = batteryKWh;
        System.out.println("  [3] ElectricCar(String,int) constructor ran, battery=" + batteryKWh);
    }

    public int getBatteryKWh() { return batteryKWh; }

    @Override public String describe() {
        return super.describe() + ", electric, battery " + batteryKWh + " kWh";
    }
}
