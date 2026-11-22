import java.time.LocalTime;
import java.time.Duration;

/** Task 8 - compile-time polymorphism (overloading), upcasting and a traced dynamic dispatch. */
public class OverloadDemo {

    /** Overloaded methods: the COMPILER chooses by the number and static types of the arguments. */
    static double fee(Vehicle v, int minutes) {
        System.out.println("  -> fee(Vehicle, int) selected at compile time");
        return v.calculateFee(minutes);
    }

    static double fee(Vehicle v, LocalTime in, LocalTime out) {
        System.out.println("  -> fee(Vehicle, LocalTime, LocalTime) selected at compile time");
        return v.calculateFee((int) Duration.between(in, out).toMinutes());
    }

    /** Overloading by DECLARED type: the choice ignores the real object. */
    static String label(Vehicle v) { return "label(Vehicle)"; }
    static String label(Car c) { return "label(Car)"; }

    public static void main(String[] args) {
        Vehicle car = new Car("TN02B2");

        System.out.println("fee(car, 130):");
        System.out.println("  = " + fee(car, 130));
        System.out.println("fee(car, 09:00, 11:10):");
        System.out.println("  = " + fee(car, LocalTime.of(9, 0), LocalTime.of(11, 10)));

        // overload resolution uses the declared type
        Vehicle declaredVehicle = new Car("TN02B2");
        Car declaredCar = new Car("TN02B2");
        System.out.println("\nlabel(declared Vehicle holding a Car) = " + label(declaredVehicle));
        System.out.println("label(declared Car)                   = " + label(declaredCar));

        // upcasting: one array, three different objects
        Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };

        System.out.println("\nDynamic method dispatch trace for v.calculateFee(130):");
        for (Vehicle v : parked) {
            System.out.println("  declared type = Vehicle, actual type = " + v.getClass().getSimpleName()
                    + ", calculateFee found in Vehicle, ratePerHour() executed in "
                    + rateOwner(v) + " -> Rs " + v.calculateFee(130));
        }
    }

    /** Which class supplies ratePerHour() for this object (found by walking up the hierarchy). */
    static String rateOwner(Vehicle v) {
        try {
            Class<?> c = v.getClass();
            while (c != null) {
                try {
                    c.getDeclaredMethod("ratePerHour");
                    return c.getSimpleName();
                } catch (NoSuchMethodException e) {
                    c = c.getSuperclass();
                }
            }
        } catch (SecurityException e) {
            // ignore
        }
        return "?";
    }
}
