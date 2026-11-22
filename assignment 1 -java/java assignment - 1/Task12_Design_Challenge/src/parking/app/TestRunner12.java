package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import parking.model.*;
import parking.service.*;

/** Task 12 tests for the new categories, the day pass and the five-floor facility. */
public class TestRunner12 {

    static LocalDateTime at(String hhmm) { return LocalDateTime.parse("2026-11-23T" + hhmm); }
    static String money(double d) { return String.valueOf(d); }
    static double van(String in, String out) { return new DeliveryVan("TN12CC1003").calculateFee(at(in), at(out)); }

    static ParkingSystem extended() {
        VehicleFactory f = new VehicleFactory();
        f.register("EBIKE", EBike::new);
        f.register("VAN", DeliveryVan::new);
        return new ParkingSystem(new ParkingFacility(new int[]{1, 2, 3, 4, 5}), f);
    }

    public static void main(String[] args) {
        System.out.printf("%-5s | %-62s | %-24s | %-24s | %s%n", "ID", "Test case", "Expected", "Actual", "Result");
        System.out.println("-".repeat(150));

        // ---- E-bike: flat fee ----
        TestRunner.check("E1", "E-bike parked 0 minutes pays the flat fee", "20.0", () -> money(new EBike("TN11BB1002").calculateFee(0)));
        TestRunner.check("E2", "E-bike parked 30 minutes", "20.0", () -> money(new EBike("TN11BB1002").calculateFee(30)));
        TestRunner.check("E3", "E-bike parked 5 hours (300 min)", "20.0", () -> money(new EBike("TN11BB1002").calculateFee(300)));
        TestRunner.check("E4", "E-bike negative duration is rejected", "IllegalArgumentException", () -> money(new EBike("TN11BB1002").calculateFee(-1)));

        // ---- Delivery van: only the overlap with 09:00-17:00 is doubled ----
        TestRunner.check("V1", "Van 10:00-12:00 fully inside window (60 + 60)", "120.0", () -> money(van("10:00", "12:00")));
        TestRunner.check("V2", "Van 07:00-08:00 fully outside window (normal car fee)", "30.0", () -> money(van("07:00", "08:00")));
        TestRunner.check("V3", "Van 08:00-10:00 enters BEFORE 9, overlap 60 min (60 + 30)", "90.0", () -> money(van("08:00", "10:00")));
        TestRunner.check("V4", "Van 16:00-18:00 leaves AFTER 17, overlap 60 min (60 + 30)", "90.0", () -> money(van("16:00", "18:00")));
        TestRunner.check("V5", "Van 17:00-18:00 starts exactly at window end (no overlap)", "30.0", () -> money(van("17:00", "18:00")));
        TestRunner.check("V6", "Van 08:00-09:00 ends exactly at window start (no overlap)", "30.0", () -> money(van("08:00", "09:00")));
        TestRunner.check("V7", "Van 09:00-17:00 whole window = exactly double the car cap", "480.0", () -> money(van("09:00", "17:00")));
        TestRunner.check("V8", "Van 08:30-09:30, 30 overlap min (30 + 15)", "45.0", () -> money(van("08:30", "09:30")));
        TestRunner.check("V9", "Van 16:45-17:15, 15 overlap min (30 + ceil(7.5)=8)", "38.0", () -> money(van("16:45", "17:15")));
        TestRunner.check("V10", "Van 23 Nov 16:00 to 24 Nov 10:00 (overlap 60 + 60, cap 240)", "300.0", () ->
                money(new DeliveryVan("TN12CC1003").calculateFee(LocalDateTime.parse("2026-11-23T16:00"), LocalDateTime.parse("2026-11-24T10:00"))));
        TestRunner.check("V11", "Van zero-minute stay inside the window", "0.0", () -> money(van("10:00", "10:00")));
        TestRunner.check("V12", "Normal car 10:00-12:00 is NOT surcharged (baseline)", "60.0", () ->
                money(new Car("TN15FF1006").calculateFee(at("10:00"), at("12:00"))));
        TestRunner.check("V13", "Peak-minute helper, stay 08:00-18:00", "480", () -> String.valueOf(DeliveryVan.peakMinutes(at("08:00"), at("18:00"))));

        // ---- Day pass ----
        TestRunner.check("D1", "Visitor with day pass parks free the same day", "0.0", () -> {
            ParkingSystem s = extended();
            s.registerPass(new DayPass("TN10AA1001", LocalDate.of(2026, 11, 23)));
            s.enter("TN10AA1001", "CAR", UserType.VISITOR, at("09:00"));
            return money(s.exit("TN10AA1001", at("15:00")).parkingFee());
        });
        TestRunner.check("D2", "Day pass for another date does not cover the stay", "30.0", () -> {
            ParkingSystem s = extended();
            s.registerPass(new DayPass("TN16GG1007", LocalDate.of(2026, 11, 24)));
            s.enter("TN16GG1007", "CAR", UserType.VISITOR, at("09:00"));
            return money(s.exit("TN16GG1007", at("10:00")).parkingFee());
        });
        TestRunner.check("D3", "Stay that crosses midnight is not covered by a one-day pass", "300.0", () -> {
            ParkingSystem s = extended();
            s.registerPass(new DayPass("TN12CC1003", LocalDate.of(2026, 11, 23)));
            s.enter("TN12CC1003", "VAN", UserType.VISITOR, at("16:00"));
            return money(s.exit("TN12CC1003", LocalDateTime.parse("2026-11-24T10:00")).parkingFee());
        });
        TestRunner.check("D4", "Pass sale price is added to the day's collection", "100.0", () -> {
            ParkingSystem s = extended();
            s.registerPass(new DayPass("TN10AA1001", LocalDate.of(2026, 11, 23)));
            return money(s.getPassSalesCollected());
        });
        TestRunner.check("D5", "Day pass with null date is rejected", "IllegalArgumentException",
                () -> { new DayPass("TN10AA1001", null); return "accepted"; });
        TestRunner.check("D6", "Day pass with invalid registration is rejected", "IllegalArgumentException",
                () -> { new DayPass("1234", LocalDate.of(2026, 11, 23)); return "accepted"; });

        // ---- Five floors with different capacities {1,2,3,4,5} ----
        TestRunner.check("F1", "Vehicle #2 goes to floor 1 slot 0 (floor 0 has 1 slot)", "1,0", () -> {
            ParkingSystem s = extended();
            s.enter("TN01AB0001", "CAR", UserType.VISITOR, at("09:00"));
            ParkingTicket t = s.enter("TN01AB0002", "CAR", UserType.VISITOR, at("09:00"));
            return t.getFloor() + "," + t.getSlot();
        });
        TestRunner.check("F2", "Vehicle #4 goes to floor 2 slot 0 (floors 0 and 1 hold 3)", "2,0", () -> {
            ParkingSystem s = extended();
            ParkingTicket t = null;
            for (int i = 1; i <= 4; i++) t = s.enter("TN01AB000" + i, "CAR", UserType.VISITOR, at("09:00"));
            return t.getFloor() + "," + t.getSlot();
        });
        TestRunner.check("F3", "Vehicle #15 takes the very last slot (floor 4 slot 4)", "4,4", () -> {
            ParkingSystem s = extended();
            ParkingTicket t = null;
            for (int i = 1; i <= 15; i++) t = s.enter("TN01AB" + String.format("%04d", i), "CAR", UserType.VISITOR, at("09:00"));
            return t.getFloor() + "," + t.getSlot();
        });
        TestRunner.check("F4", "Vehicle #16 is refused: all 15 slots are taken", "IllegalStateException", () -> {
            ParkingSystem s = extended();
            for (int i = 1; i <= 16; i++) s.enter("TN01AB" + String.format("%04d", i), "CAR", UserType.VISITOR, at("09:00"));
            return "accepted";
        });
        TestRunner.check("F5", "Production facility has 5 floors and 200 slots", "5/200", () -> {
            ParkingFacility f = new ParkingFacility(ParkingApp12.FLOORS);
            return f.getFloorCount() + "/" + f.totalCapacity();
        });

        // ---- Old categories still behave exactly as in Task 11 ----
        TestRunner.check("R1", "Regression: car 130 min unchanged", "90.0", () -> money(new Car("TN02B2").calculateFee(130)));
        TestRunner.check("R2", "Regression: EV 130 min + 10 kWh unchanged", "210.0", () -> {
            ParkingSystem s = extended();
            s.enter("TN03C3", "EV", UserType.VISITOR, at("09:00"));
            return money(s.exit("TN03C3", at("11:10"), 10).total());
        });
        TestRunner.check("R3", "Regression: unknown category still rejected", "IllegalArgumentException",
                () -> { extended().enter("TN01AB1111", "BUS", UserType.VISITOR, at("09:00")); return "accepted"; });
        TestRunner.check("R4", "Regression: duplicate registration still rejected", "IllegalStateException", () -> {
            ParkingSystem s = extended();
            s.enter("TN01AB1111", "VAN", UserType.VISITOR, at("09:00"));
            s.enter("TN01AB1111", "EBIKE", UserType.VISITOR, at("09:10"));
            return "accepted";
        });

        // ---- Scenario (hand calculation Rs 558 = parking 358 + pass sales 200) ----
        TestRunner.check("S1", "Scenario file total collection (hand calculation Rs 558)", "558.0", () -> {
            try {
                CommandProcessor p = ParkingApp12.build();
                for (String line : java.nio.file.Files.readAllLines(java.nio.file.Paths.get("input/scenario12.txt"))) p.process(line);
                return money(p.getSystem().getTotalCollection());
            } catch (java.io.IOException e) {
                throw new IllegalStateException("scenario file missing");
            }
        });

        if (!TestRunner.summary()) System.exit(1);
    }
}
