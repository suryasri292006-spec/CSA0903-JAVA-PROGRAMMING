package parking.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import parking.service.ParkingFacility;
import parking.service.ParkingSystem;
import parking.service.VehicleFactory;

/**
 * Campus Smart-Parking System - console application.
 *   java parking.app.ParkingApp input/scenario.txt   reads commands from a file
 *   java parking.app.ParkingApp                      reads commands typed by the user (type QUIT to stop)
 */
public class ParkingApp {

    /** Runs every line and prints the result; echo=true prints each command first (for files). */
    public static void run(CommandProcessor processor, List<String> lines, boolean echo) {
        for (String line : lines) {
            String result = processor.process(line);
            if (result == null) continue;
            if (echo) System.out.println("> " + line.trim());
            System.out.println(result);
        }
    }

    public static void main(String[] args) throws IOException {
        // 3 floors x 50 slots, as in the problem statement
        ParkingSystem system = new ParkingSystem(new ParkingFacility(new int[]{50, 50, 50}), new VehicleFactory());
        CommandProcessor processor = new CommandProcessor(system);

        if (args.length > 0) {
            run(processor, Files.readAllLines(Paths.get(args[0])), true);
        } else {
            System.out.println("Campus Smart Parking - type commands (ENTER, EXIT, PASS_MONTHLY, STATUS, REPORT, QUIT)");
            Scanner in = new Scanner(System.in);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.trim().equalsIgnoreCase("QUIT")) break;
                String result = processor.process(line);
                if (result != null) System.out.println(result);
            }
        }
    }
}
