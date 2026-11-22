package parking.app;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import parking.model.Car;
import parking.model.ParkingTicket;
import parking.model.UserType;

/** Task 11 alternative: find a ticket on exit in an array, an ArrayList or a HashMap keyed by regNo. */
public class TicketStoreBenchmark {

    static String reg(int i) {   // unique, valid registration numbers
        return "TN01" + (char) ('A' + i / 10000 / 26 % 26) + (char) ('A' + i / 10000 % 26) + String.format("%04d", i % 10000);
    }

    public static void main(String[] args) {
        int[] sizes = {1_000, 10_000, 100_000};
        int lookups = 200;
        LocalDateTime t0 = LocalDateTime.parse("2026-11-23T09:00");

        System.out.println("Finding the LAST parked vehicle on exit, " + lookups + " lookups per structure");
        System.out.printf("%-9s %-16s %-16s %-16s %s%n", "n", "array scan", "ArrayList scan", "HashMap get", "approx. extra memory of HashMap");
        for (int n : sizes) {
            ParkingTicket[] array = new ParkingTicket[n];
            List<ParkingTicket> list = new ArrayList<>();
            Map<String, ParkingTicket> map = new HashMap<>();
            for (int i = 0; i < n; i++) {
                ParkingTicket t = new ParkingTicket(new Car(reg(i)), UserType.VISITOR, 0, i % 50, t0);
                array[i] = t; list.add(t); map.put(t.getVehicle().getRegNo(), t);
            }
            String key = reg(n - 1);
            long sink = 0;

            long s = System.nanoTime();
            for (int k = 0; k < lookups; k++) {
                for (ParkingTicket t : array) if (t.getVehicle().getRegNo().equals(key)) { sink += t.getSlot(); break; }
            }
            double a = (System.nanoTime() - s) / 1e6;

            s = System.nanoTime();
            for (int k = 0; k < lookups; k++) {
                for (ParkingTicket t : list) if (t.getVehicle().getRegNo().equals(key)) { sink += t.getSlot(); break; }
            }
            double l = (System.nanoTime() - s) / 1e6;

            s = System.nanoTime();
            for (int k = 0; k < lookups; k++) sink += map.get(key).getSlot();
            double h = (System.nanoTime() - s) / 1e6;

            System.out.printf("%-9d %-16s %-16s %-16s ~%d KB (table + entries, about 48 bytes per vehicle)  (sink %d)%n",
                    n, String.format("%.2f ms", a), String.format("%.2f ms", l), String.format("%.3f ms", h), n * 48 / 1024, sink);
        }
    }
}
