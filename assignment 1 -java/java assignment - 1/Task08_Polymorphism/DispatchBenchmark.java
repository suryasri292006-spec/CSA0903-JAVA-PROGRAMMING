/** Task 8 - alternative: instanceof + casting chain versus dynamic dispatch (k = 3 types). */
public class DispatchBenchmark {

    static double feeByInstanceof(Vehicle v, int minutes) {
        int hours = (minutes + 59) / 60;
        if (v instanceof ElectricCar) return hours * 30;       // must test the most specific type first
        else if (v instanceof Car) return hours * 30;
        else if (v instanceof TwoWheeler) return hours * 10;
        throw new IllegalArgumentException("unknown type");     // a NEW type silently falls here
    }

    public static void main(String[] args) {
        Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };
        int rounds = 100_000_000;
        double sink = 0;

        for (int w = 0; w < 3; w++) {                              // warm-up so the JIT does not bias round 1
            for (int i = 0; i < 5_000_000; i++) { sink += parked[i % 3].calculateFee(130); sink += feeByInstanceof(parked[i % 3], 130); }
        }

        long t = System.nanoTime();
        for (int i = 0; i < rounds; i++) { sink += parked[i % 3].calculateFee(130); }
        long dispatchMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        for (int i = 0; i < rounds; i++) { sink += feeByInstanceof(parked[i % 3], 130); }
        long instanceofMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println(rounds + " fee calculations: dynamic dispatch = " + dispatchMs
                + " ms, instanceof chain = " + instanceofMs + " ms (sink " + sink + ")");
    }
}
