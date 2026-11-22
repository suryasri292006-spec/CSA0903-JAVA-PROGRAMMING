/** Task 6 - subclass of Vehicle and parent of ElectricCar (multilevel inheritance). */
public class Car extends Vehicle {
    public Car(String regNo) {
        super(regNo);
        System.out.println("  [2] Car(String) constructor ran");
    }
    @Override protected double ratePerHour() { return 30; }

    @Override public String describe() {
        return super.describe() + ", category Car";          // super used for a METHOD
    }
}
