package parking.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Scanner;
import parking.model.DayPass;
import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.service.ParkingFacility;
import parking.service.ParkingSystem;
import parking.service.VehicleFactory;

/**
 * NEW (Task 12): wires the extended campus system. Nothing in the existing classes is edited:
 * new categories are REGISTERED, the new command is REGISTERED, and the five floors are just a bigger array.
 */
public class ParkingApp12 {

    /** Five floors with a different number of slots on each floor. */
    public static final int[] FLOORS = {20, 30, 40, 50, 60};

    public static CommandProcessor build() {
        VehicleFactory factory = new VehicleFactory();
        factory.register("EBIKE", EBike::new);              // new vehicle category 1
        factory.register("VAN", DeliveryVan::new);          // new vehicle category 2

        ParkingSystem system = new ParkingSystem(new ParkingFacility(FLOORS), factory);
        CommandProcessor processor = new CommandProcessor(system);

        processor.register("PASS_DAY", args -> {            // new command: visitor buys a one-day pass
            DayPass pass = new DayPass(args[0], LocalDate.parse(args[1]));
            system.registerPass(pass);
            return "OK: " + pass.describe();
        });
        return processor;
    }

    public static void main(String[] args) throws IOException {
        CommandProcessor processor = build();
        if (args.length > 0) {
            ParkingApp.run(processor, Files.readAllLines(Paths.get(args[0])), true);
        } else {
            System.out.println("Campus Smart Parking v2 - categories: TWOWHEELER, CAR, EV, EBIKE, VAN; type QUIT to stop");
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
