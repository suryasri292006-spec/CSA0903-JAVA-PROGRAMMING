package parking.app;

import parking.model.*;
import parking.service.*;
import java.time.*;

public class Main {
    static int passed = 0, failed = 0;
    static final LocalDate D = LocalDate.of(2026, 11, 21);

    static LocalDateTime at(int h, int m) { return D.atTime(h, m); }

    static void check(String name, Object expected, Object actual) {
        boolean ok = expected.equals(actual);
        if (ok) passed++; else failed++;
        System.out.printf("%-48s expected=%-8s actual=%-8s %s%n",
                name, expected, actual, ok ? "PASS" : "FAIL");
    }

    static void expectThrows(String name, Class<? extends Exception> type, Runnable r) {
        try { r.run(); check(name, type.getSimpleName(), "no exception"); }
        catch (Exception e) { check(name, type.getSimpleName(), e.getClass().getSimpleName()); }
    }

    public static void main(String[] args) {
        System.out.println("=== Fee calculation (normal + boundary) ===");
        check("Two-wheeler 130 min", 30.0, new TwoWheeler("A1").calculateFee(130));
        check("Car 130 min", 90.0, new Car("A2").calculateFee(130));
        check("Car 0 min", 0.0, new Car("A2").calculateFee(0));
        check("Car 60 min (slab boundary)", 30.0, new Car("A2").calculateFee(60));
        check("Car 61 min (just over boundary)", 60.0, new Car("A2").calculateFee(61));
        check("Car 09:00-10:00 (LocalTime overload)", 30.0,
                new Car("A2").calculateFee(LocalTime.of(9, 0), LocalTime.of(10, 0)));
        check("E-bike 300 min flat", 20.0, new EBike("E1").calculateFee(300));
        check("E-bike 0 min", 0.0, new EBike("E1").calculateFee(0));
        check("Van 08:00-09:00 (off-peak)", 30.0,
                new DeliveryVan("V1").calculateFee(LocalTime.of(8, 0), LocalTime.of(9, 0)));
        check("Van 09:00-10:00 (peak, double)", 60.0,
                new DeliveryVan("V1").calculateFee(LocalTime.of(9, 0), LocalTime.of(10, 0)));
        check("Van 16:30-17:30 (peak hour + off-peak hour)", 90.0,
                new DeliveryVan("V1").calculateFee(LocalTime.of(16, 30), LocalTime.of(17, 30)));
        check("Car equals EV with same regNo", true, new Car("X").equals(new ElectricCar("X")));

        System.out.println("\n=== Invalid cases ===");
        expectThrows("Unknown vehicle type", IllegalArgumentException.class,
                () -> VehicleFactory.create("HELICOPTER", "H1"));
        expectThrows("Negative duration", IllegalArgumentException.class,
                () -> new Car("N1").calculateFee(-5));
        ParkingService dup = new ParkingService(new int[]{2, 1});
        dup.enter(new Car("D1"), Pass.NONE, at(8, 0));
        expectThrows("Duplicate registration", IllegalArgumentException.class,
                () -> dup.enter(new Car("D1"), Pass.NONE, at(8, 5)));
        check("Invalid slot release", false, new ParkingFacility(new int[]{2}).release(9, 0));
        expectThrows("Exit of unknown vehicle", IllegalArgumentException.class,
                () -> dup.exit("NOPE", at(9, 0)));

        System.out.println("\n=== Boundary: last free slot (floors 2+1) ===");
        ParkingService small = new ParkingService(new int[]{2, 1});
        small.enter(new Car("S1"), Pass.NONE, at(8, 0));
        small.enter(new Car("S2"), Pass.NONE, at(8, 0));
        ParkingTicket last = small.enter(new Car("S3"), Pass.NONE, at(8, 0));
        check("Last slot is floor 1 slot 0", "1,0", last.getFloor() + "," + last.getSlot());
        expectThrows("Facility full", IllegalStateException.class,
                () -> small.enter(new Car("S4"), Pass.NONE, at(8, 0)));
        small.exit("S1", at(9, 0));
        ParkingTicket reuse = small.enter(new Car("S4"), Pass.NONE, at(9, 5));
        check("Freed slot 0,0 is reused", "0,0", reuse.getFloor() + "," + reuse.getSlot());

        System.out.println("\n=== Passes ===");
        ParkingService ps = new ParkingService(new int[]{3, 2});
        ps.enter(new Car("P1"), new MonthlyPass(D.minusDays(10), D.plusDays(20)), at(9, 0));
        check("Valid monthly pass is free", 0.0, ps.exit("P1", at(11, 0)));
        ps.enter(new Car("P2"), new MonthlyPass(D.minusDays(60), D.minusDays(30)), at(9, 0));
        check("Expired monthly pass pays full (2h)", 60.0, ps.exit("P2", at(11, 0)));
        ps.enter(new Car("P3"), new DayPass(D), at(9, 0));
        check("Visitor day pass (today) is free", 0.0, ps.exit("P3", at(12, 0)));
        ps.enter(new Car("P4"), new DayPass(D.minusDays(1)), at(9, 0));
        check("Day pass for another date pays", 30.0, ps.exit("P4", at(10, 0)));

        System.out.println("\n=== Day scenario (hand calculation = Rs 400) ===");
        ParkingService day = new ParkingService(new int[]{3, 2, 4});
        day.enter(VehicleFactory.create("twowheeler", "TN01"), Pass.NONE, at(8, 0));
        day.enter(VehicleFactory.create("car", "TN02"), Pass.NONE, at(9, 0));
        day.enter(VehicleFactory.create("ev", "TN03"), Pass.NONE, at(10, 0));
        day.enter(VehicleFactory.create("ebike", "TN04"), Pass.NONE, at(10, 0));
        day.enter(VehicleFactory.create("van", "TN05"), Pass.NONE, at(8, 30));
        day.enter(VehicleFactory.create("car", "TN06"),
                new MonthlyPass(D.minusDays(5), D.plusDays(25)), at(9, 0));
        check("TN01 two-wheeler 08:00-09:30 (2h x 10)", 20.0, day.exit("TN01", at(9, 30)));
        check("TN02 car 09:00-12:01 (4h x 30)", 120.0, day.exit("TN02", at(12, 1)));
        check("TN03 EV 10:00-11:00 + 10 kWh (30 + 120)", 150.0, day.exit("TN03", at(11, 0), 10));
        check("TN04 e-bike flat", 20.0, day.exit("TN04", at(14, 0)));
        check("TN05 van 08:30-10:30 (30 + 60)", 90.0, day.exit("TN05", at(10, 30)));
        check("TN06 monthly pass", 0.0, day.exit("TN06", at(17, 0)));
        check("Total collection for the day", 400.0, day.getTotalCollection());
        check("No vehicles left inside", 0, day.activeCount());

        System.out.println("\nPassed: " + passed + "  Failed: " + failed);
    }
}
