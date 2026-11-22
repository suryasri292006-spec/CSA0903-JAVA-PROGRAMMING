import java.util.HashSet;
import java.util.Set;

/** Task 7 - demonstrates overriding, final, toString, equals and hashCode. */
public class OverrideDemo {
    public static void main(String[] args) {
        Vehicle bike = new TwoWheeler("TN01A1");
        Vehicle car = new Car("TN02B2");

        System.out.println(bike + " fee for 600 min = " + bike.calculateFee(600));
        System.out.println(car + " fee for 600 min = " + car.calculateFee(600) + "  (capped at 240 by the Car override)");

        // equals / hashCode contract
        Vehicle a = new Car("TN02B2");
        Vehicle b = new Car("TN02B2");
        Vehicle other = new Car("TN99Z9");
        System.out.println("a == b          : " + (a == b));
        System.out.println("a.equals(b)     : " + a.equals(b));
        System.out.println("same hashCode   : " + (a.hashCode() == b.hashCode()));
        System.out.println("a.equals(other) : " + a.equals(other));
        System.out.println("a.equals(null)  : " + a.equals(null));

        // Why hashCode matters: a HashSet rejects duplicates only if both methods are consistent
        Set<Vehicle> parked = new HashSet<>();
        System.out.println("add first TN02B2 : " + parked.add(a));
        System.out.println("add second TN02B2: " + parked.add(b) + "  (duplicate rejected)");
        System.out.println("Set size = " + parked.size());
        System.out.println("toString: " + parked);
    }
}
