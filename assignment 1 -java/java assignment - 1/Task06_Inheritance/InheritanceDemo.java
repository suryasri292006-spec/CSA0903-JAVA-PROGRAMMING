/** Task 6 - shows what is inherited and the constructor execution order. */
public class InheritanceDemo {
    public static void main(String[] args) {
        System.out.println("new TwoWheeler(\"TN01A1\"):");
        Vehicle w = new TwoWheeler("TN01A1");

        System.out.println("new Car(\"TN02B2\"):");
        Vehicle c = new Car("TN02B2");

        System.out.println("new ElectricCar(\"TN03C3\"):");
        ElectricCar e = new ElectricCar("TN03C3");

        System.out.println();
        System.out.println(w.describe());
        System.out.println(c.describe());
        System.out.println(e.describe());
        System.out.println("Inherited getRegNo() on ElectricCar: " + e.getRegNo());
        System.out.println("e instanceof Car     : " + (e instanceof Car));
        System.out.println("e instanceof Vehicle : " + (e instanceof Vehicle));
    }
}
