/** Task 9 - uses the abstract class, the interfaces and a default/static interface method. */
public class AbstractionDemo {

    /** Visitor pays the full fee. */
    static class Visitor implements Payable {
        @Override public double amountPayable(double parkingFee) { return parkingFee; }
    }

    /** Student/staff with a valid monthly pass park free. */
    static class PassHolder implements Payable {
        @Override public double amountPayable(double parkingFee) { return 0; }
    }

    public static void main(String[] args) {
        // Vehicle v = new Vehicle("X");   // would NOT compile: Vehicle is abstract
        Vehicle[] vehicles = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };

        Payable visitor = new Visitor();
        Payable student = new PassHolder();

        for (Vehicle v : vehicles) {
            double fee = v.calculateFee(130);
            System.out.println(v.getClass().getSimpleName() + " " + v.getRegNo()
                    + " parking fee=" + fee
                    + ", visitor pays=" + visitor.amountPayable(fee)
                    + ", pass holder pays=" + student.amountPayable(fee));
            if (v instanceof Chargeable c) {
                int kWh = 10;
                System.out.println("   charging valid? " + Chargeable.isValidKwh(kWh)       // static interface method
                        + ", charging=" + c.chargingFee(kWh)
                        + ", with service charge=" + c.chargingFeeWithServiceCharge(kWh));  // default method
            }
        }
        System.out.println("Chargeable.isValidKwh(500) = " + Chargeable.isValidKwh(500));
    }
}
