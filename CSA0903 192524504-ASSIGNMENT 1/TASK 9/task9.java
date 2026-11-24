
interface Chargeable {
    double chargingFee(int kWh);

    // Default method
    default void chargingStatus() {
        System.out.println("Electric vehicle charging is available.");
    }

    // Static method
    static double standardChargingRate() {
        return 12.0;
    }
}

interface Payable {
    void makePayment(double amount);

    // Default method
    default void paymentStatus() {
        System.out.println("Payment completed successfully.");
    }
}

abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    // Abstract method
    protected abstract double ratePerHour();

    // Concrete method
    public double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}

class TwoWheeler extends Vehicle {

    TwoWheeler(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 10.0;
    }
}

class Car extends Vehicle {

    Car(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }
}

class ElectricCar extends Car implements Chargeable, Payable {

    ElectricCar(String regNo) {
        super(regNo);
    }

    @Override
    public double chargingFee(int kWh) {
        return kWh * Chargeable.standardChargingRate();
    }

    @Override
    public void makePayment(double amount) {
        System.out.println("Payment Amount: Rs " + amount);
    }
}

public class Main {

    public static void main(String[] args) {

        Vehicle[] parked = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };

        System.out.println("CAMPUS SMART PARKING SYSTEM");
        System.out.println("---------------------------");

        for (Vehicle v : parked) {

            double fee = v.calculateFee(130);

            if (v instanceof Chargeable) {
                Chargeable c = (Chargeable) v;
                fee += c.chargingFee(10);
            }

            System.out.println(
                v.getClass().getSimpleName() + " "
                + v.getRegNo() + " -> Rs " + fee
            );

            if (v instanceof Payable) {
                Payable p = (Payable) v;
                p.makePayment(fee);
                p.paymentStatus();
            }

            if (v instanceof Chargeable) {
                Chargeable c = (Chargeable) v;
                c.chargingStatus();
            }
        }

        System.out.println("\nStandard Charging Rate: Rs "
                + Chargeable.standardChargingRate()
                + " per kWh");
    }
}
