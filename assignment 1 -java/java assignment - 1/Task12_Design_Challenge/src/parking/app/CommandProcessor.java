package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import parking.model.MonthlyPass;
import parking.model.ParkingTicket;
import parking.model.Receipt;
import parking.model.UserType;
import parking.service.ParkingSystem;

/**
 * Reads text commands and turns them into calls on ParkingSystem.
 * Commands live in a table, so new commands can be added with register(...).
 *
 *   ENTER <regNo> <category> <userType> <yyyy-MM-ddTHH:mm>
 *   EXIT <regNo> <yyyy-MM-ddTHH:mm> [kWh]
 *   PASS_MONTHLY <regNo> <STUDENT|STAFF> <fromDate> <toDate>
 *   STATUS
 *   REPORT
 */
public class CommandProcessor {

    /** One command: receives the words after the command name, returns the message to print. */
    public interface CommandHandler {
        String handle(String[] args);
    }

    private final ParkingSystem system;
    private final Map<String, CommandHandler> handlers = new LinkedHashMap<>();

    public CommandProcessor(ParkingSystem system) {
        this.system = system;
        register("ENTER", this::enter);
        register("EXIT", this::exit);
        register("PASS_MONTHLY", this::monthlyPass);
        register("STATUS", args -> system.availabilityReport());
        register("REPORT", this::report);
    }

    public void register(String name, CommandHandler handler) {
        handlers.put(name.toUpperCase(), handler);
    }

    public ParkingSystem getSystem() {
        return system;
    }

    /** Runs one line. Returns null for blank lines and comments, otherwise a message (errors start with ERROR:). */
    public String process(String line) {
        if (line == null) return null;
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null;
        }
        String[] parts = trimmed.split("\\s+");
        CommandHandler handler = handlers.get(parts[0].toUpperCase());
        if (handler == null) {
            return "ERROR: Unknown command: " + parts[0];
        }
        try {
            return handler.handle(Arrays.copyOfRange(parts, 1, parts.length));
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERROR: Missing arguments for " + parts[0].toUpperCase();
        } catch (DateTimeParseException e) {
            return "ERROR: Invalid date/time: " + e.getParsedString();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // ---------------- built-in commands ----------------

    private String enter(String[] a) {
        ParkingTicket t = system.enter(a[0], a[1], UserType.parse(a[2]), LocalDateTime.parse(a[3]));
        return "OK: " + t.getTicketNo() + " issued to " + t.getVehicle().getRegNo()
                + " (" + t.getVehicle().getCategory() + ") slot floor " + t.getFloor() + " / slot " + t.getSlot();
    }

    private String exit(String[] a) {
        int kWh = a.length > 2 ? Integer.parseInt(a[2]) : 0;
        Receipt r = system.exit(a[0], LocalDateTime.parse(a[1]), kWh);
        return String.format("OK: %s %s left after %d min | parking Rs %.2f%s + charging Rs %.2f = Rs %.2f",
                r.ticketNo(), r.regNo(), r.minutes(), r.parkingFee(),
                r.passApplied() ? " (pass applied)" : "", r.chargingFee(), r.total());
    }

    private String monthlyPass(String[] a) {
        MonthlyPass p = new MonthlyPass(a[0], UserType.parse(a[1]), LocalDate.parse(a[2]), LocalDate.parse(a[3]));
        system.registerPass(p);
        return "OK: " + p.describe();
    }

    private String report(String[] a) {
        return String.format("Parking fees Rs %.2f | Charging fees Rs %.2f | Pass sales Rs %.2f | TOTAL Rs %.2f | vehicles still parked %d",
                system.getParkingFeesCollected(), system.getChargingFeesCollected(),
                system.getPassSalesCollected(), system.getTotalCollection(), system.getActiveCount());
    }
}
