package parking.app;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import parking.model.*;
import parking.service.*;

/** Runs the test table of Task 11: normal, boundary and invalid cases, expected versus actual. */
public class TestRunner {
    private static int total = 0;
    private static int passed = 0;

    /** Records one test. A thrown exception is reported as the exception's simple class name. */
    public static void check(String id, String description, String expected, Supplier<String> action) {
        String actual;
        try {
            actual = action.get();
        } catch (RuntimeException e) {
            actual = e.getClass().getSimpleName();
        }
        total++;
        boolean ok = expected.equals(actual);
        if (ok) passed++;
        System.out.printf("%-5s | %-62s | %-24s | %-24s | %s%n", id, description, expected, actual, ok ? "PASS" : "FAIL");
    }

    public static boolean summary() {
        System.out.println("\nResult: " + passed + " of " + total + " tests passed");
        return passed == total;
    }

    static LocalDateTime at(String hhmm) {
        return LocalDateTime.parse("2026-11-23T" + hhmm);
    }

    static ParkingSystem system(int... floors) {
        return new ParkingSystem(new ParkingFacility(floors), new VehicleFactory());
    }

    static String money(double d) {
        return String.valueOf(d);
    }

    public static void main(String[] args) throws Exception {
        System.out.printf("%-5s | %-62s | %-24s | %-24s | %s%n", "ID", "Test case", "Expected", "Actual", "Result");
        System.out.println("-".repeat(150));

        // ---------------- NORMAL ----------------
        check("N1", "Two-wheeler parked 120 min (2 h x Rs 10)", "20.0",
                () -> money(new TwoWheeler("TN01A1").calculateFee(120)));
        check("N2", "Car parked 130 min (3 started hours x Rs 30)", "90.0",
                () -> money(new Car("TN02B2").calculateFee(130)));
        check("N3", "EV 130 min + 10 kWh charging (90 + 120)", "210.0", () -> {
            ParkingSystem s = system(5);
            s.enter("TN03C3", "EV", UserType.VISITOR, at("09:00"));
            return money(s.exit("TN03C3", at("11:10"), 10).total());
        });
        check("N4", "Two entries receive different ticket numbers", "true", () -> {
            ParkingSystem s = system(5);
            ParkingTicket a = s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            ParkingTicket b = s.enter("TN02CD2222", "CAR", UserType.VISITOR, at("09:00"));
            return String.valueOf(!a.getTicketNo().equals(b.getTicketNo()));
        });
        check("N5", "Slot is released on exit and given to the next vehicle", "0,0", () -> {
            ParkingSystem s = system(1);
            s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            s.exit("TN01AB1111", at("10:00"));
            ParkingTicket t = s.enter("TN02CD2222", "CAR", UserType.VISITOR, at("10:05"));
            return t.getFloor() + "," + t.getSlot();
        });
        check("N6", "Student with valid monthly pass parks free (4 h car)", "0.0", () -> {
            ParkingSystem s = system(5);
            s.registerPass(new MonthlyPass("TN01AB1001", UserType.STUDENT, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)));
            s.enter("TN01AB1001", "CAR", UserType.STUDENT, at("09:00"));
            return money(s.exit("TN01AB1001", at("13:00")).parkingFee());
        });
        check("N7", "Visitor pays the full fee (car, 60 min)", "30.0", () -> {
            ParkingSystem s = system(5);
            s.enter("TN04GH4004", "CAR", UserType.VISITOR, at("09:00"));
            return money(s.exit("TN04GH4004", at("10:00")).parkingFee());
        });
        check("N8", "Staff WITHOUT a pass pays the full fee (car, 60 min)", "30.0", () -> {
            ParkingSystem s = system(5);
            s.enter("TN05JK5005", "CAR", UserType.STAFF, at("09:00"));
            return money(s.exit("TN05JK5005", at("10:00")).parkingFee());
        });
        check("N9", "Expired pass does not waive the fee", "30.0", () -> {
            ParkingSystem s = system(5);
            s.registerPass(new MonthlyPass("TN06LM6006", UserType.STAFF, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31)));
            s.enter("TN06LM6006", "CAR", UserType.STAFF, at("09:00"));
            return money(s.exit("TN06LM6006", at("10:00")).parkingFee());
        });
        check("N10", "Availability display after one entry (floor 0 of {2,1})", "true", () -> {
            ParkingSystem s = system(2, 1);
            s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            return String.valueOf(s.availabilityReport().contains("Floor 0: 1/2 free") && s.availabilityReport().contains("Total: 2/3 free"));
        });

        // ---------------- BOUNDARY ----------------
        check("B1", "Zero-minute parking", "0.0", () -> money(new Car("TN01AB1111").calculateFee(0)));
        check("B2", "Exactly one hour (60 min) is one slab", "30.0", () -> money(new Car("TN01AB1111").calculateFee(60)));
        check("B3", "61 minutes starts a second hour", "60.0", () -> money(new Car("TN01AB1111").calculateFee(61)));
        check("B4", "Exactly 8 hours = daily maximum reached", "240.0", () -> money(new Car("TN01AB1111").calculateFee(480)));
        check("B5", "481 minutes still capped at the daily maximum", "240.0", () -> money(new Car("TN01AB1111").calculateFee(481)));
        check("B6", "Exactly 24 hours = one daily maximum", "240.0", () -> money(new Car("TN01AB1111").calculateFee(1440)));
        check("B7", "1441 minutes = one day + one started hour", "270.0", () -> money(new Car("TN01AB1111").calculateFee(1441)));
        check("B8", "Two-wheeler daily maximum (10 h parked)", "80.0", () -> money(new TwoWheeler("TN01A1").calculateFee(600)));
        check("B9", "Last free slot is allocated, then facility is full", "0,0|IllegalStateException", () -> {
            ParkingSystem s = system(1);
            ParkingTicket t = s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            String first = t.getFloor() + "," + t.getSlot();
            try {
                s.enter("TN02CD2222", "CAR", UserType.VISITOR, at("09:05"));
                return first + "|accepted";
            } catch (IllegalStateException e) {
                return first + "|" + e.getClass().getSimpleName();
            }
        });
        check("B10", "Pass valid on its last day (23 Nov) still waives the fee", "0.0", () -> {
            ParkingSystem s = system(5);
            s.registerPass(new MonthlyPass("TN01AB1001", UserType.STUDENT, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 23)));
            s.enter("TN01AB1001", "CAR", UserType.STUDENT, at("08:00"));
            return money(s.exit("TN01AB1001", at("23:59")).parkingFee());
        });

        // ---------------- INVALID ----------------
        check("I1", "Unknown vehicle type BUS", "IllegalArgumentException",
                () -> { system(5).enter("TN01AB1111", "BUS", UserType.VISITOR, at("09:00")); return "accepted"; });
        check("I2", "Duplicate registration number", "IllegalStateException", () -> {
            ParkingSystem s = system(5);
            s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            s.enter("tn01ab1111", "TWOWHEELER", UserType.VISITOR, at("09:10"));   // same number, other case
            return "accepted";
        });
        check("I3", "Invalid registration number format", "IllegalArgumentException",
                () -> { system(5).enter("12AB", "CAR", UserType.VISITOR, at("09:00")); return "accepted"; });
        check("I4", "Missing (null) registration number", "IllegalArgumentException",
                () -> { system(5).enter(null, "CAR", UserType.VISITOR, at("09:00")); return "accepted"; });
        check("I5", "Release of invalid slot number (floor 5 of 2 floors)", "false",
                () -> String.valueOf(new ParkingFacility(new int[]{2, 1}).release(5, 0)));
        check("I6", "Release of negative slot index", "false",
                () -> String.valueOf(new ParkingFacility(new int[]{2, 1}).release(0, -1)));
        check("I7", "Release of a slot that is already free", "false",
                () -> String.valueOf(new ParkingFacility(new int[]{2, 1}).release(0, 0)));
        check("I8", "Exit of a vehicle that never entered", "IllegalArgumentException",
                () -> { system(5).exit("TN99ZZ9999", at("10:00")); return "accepted"; });
        check("I9", "Exit time earlier than entry time", "IllegalArgumentException", () -> {
            ParkingSystem s = system(5);
            s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("10:00"));
            s.exit("TN01AB1111", at("09:00"));
            return "accepted";
        });
        check("I10", "Negative charging energy", "IllegalArgumentException", () -> {
            ParkingSystem s = system(5);
            s.enter("TN03C3", "EV", UserType.VISITOR, at("09:00"));
            s.exit("TN03C3", at("10:00"), -5);
            return "accepted";
        });
        check("I11", "Charging requested for a non-EV car", "IllegalArgumentException", () -> {
            ParkingSystem s = system(5);
            s.enter("TN01AB1111", "CAR", UserType.VISITOR, at("09:00"));
            s.exit("TN01AB1111", at("10:00"), 10);
            return "accepted";
        });
        check("I12", "Monthly pass requested for a visitor", "IllegalArgumentException",
                () -> { new MonthlyPass("TN01AB1111", UserType.VISITOR, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)); return "accepted"; });
        check("I13", "Unknown user type", "IllegalArgumentException", () -> { UserType.parse("GUEST"); return "accepted"; });
        check("I14", "Negative duration", "IllegalArgumentException", () -> money(new Car("TN01AB1111").calculateFee(-1)));
        check("I15", "Unknown command text", "ERROR: Unknown command: FLY", () -> new CommandProcessor(system(5)).process("FLY away"));
        check("I16", "Invalid date text in a command", "ERROR: Invalid date/time: yesterday", () ->
                new CommandProcessor(system(5)).process("ENTER TN01AB1111 CAR VISITOR yesterday"));
        check("I17", "Missing arguments in a command", "ERROR: Missing arguments for ENTER", () ->
                new CommandProcessor(system(5)).process("ENTER TN01AB1111"));

        // ---------------- DAILY COLLECTION SCENARIO (hand calculated = Rs 560) ----------------
        check("S1", "Scenario file total collection (hand calculation Rs 560)", "560.0", () -> {
            try {
                ParkingSystem s = new ParkingSystem(new ParkingFacility(new int[]{50, 50, 50}), new VehicleFactory());
                CommandProcessor p = new CommandProcessor(s);
                List<String> lines = Files.readAllLines(Paths.get("input/scenario.txt"));
                for (String line : lines) p.process(line);
                return money(s.getTotalCollection());
            } catch (java.io.IOException e) {
                throw new IllegalStateException("scenario file missing");
            }
        });

        boolean allPassed = summary();
        if (!allPassed) System.exit(1);
    }
}
