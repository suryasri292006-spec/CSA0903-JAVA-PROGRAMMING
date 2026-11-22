/**
 * Task 3 - fee computation using if-else, switch, a loop and early return.
 * Slabs: first hour = rate, each additional started hour = rate,
 * daily maximum = 8 x rate for every full or partial 24 hours.
 */
public class FeeCalculator {

    /** Rate per hour chosen with a switch on the vehicle type. */
    static int ratePerHour(String type) {
        switch (type) {
            case "TWOWHEELER": return 10;
            case "CAR":        return 30;
            case "EV":         return 30;
            default:           return -1;        // unknown type
        }
    }

    /** Returns the fee in rupees, or -1 for invalid input. */
    static int computeFee(String type, int minutes) {
        int rate = ratePerHour(type);
        if (rate < 0 || minutes < 0) {           // invalid input: stop immediately
            return -1;
        }
        if (minutes == 0) {                      // nothing to charge
            return 0;
        }
        int dailyMax = 8 * rate;
        int fee = 0;
        int remaining = minutes;
        while (remaining > 0) {                  // one iteration per 24-hour block
            int blockMinutes = Math.min(remaining, 24 * 60);
            int hours = (blockMinutes + 59) / 60;          // integer ceiling
            int blockFee;
            if (hours == 1) {                              // first-hour slab
                blockFee = rate;
            } else {                                       // additional hours slab
                blockFee = rate + (hours - 1) * rate;
            }
            if (blockFee > dailyMax) {                     // daily maximum slab
                blockFee = dailyMax;
            }
            fee += blockFee;
            remaining -= blockMinutes;
        }
        return fee;
    }

    public static void main(String[] args) {
        String[] types = {"TWOWHEELER", "CAR", "EV", "BUS"};
        int[] minutes = {0, 30, 60, 61, 130, 480, 481, 1440, 1441};
        System.out.printf("%-11s", "minutes");
        for (String t : types) System.out.printf("%12s", t);
        System.out.println();
        for (int m : minutes) {
            System.out.printf("%-11d", m);
            for (String t : types) System.out.printf("%12d", computeFee(t, m));
            System.out.println();
        }
        System.out.println("Negative minutes (CAR, -5) -> " + computeFee("CAR", -5));
    }
}
