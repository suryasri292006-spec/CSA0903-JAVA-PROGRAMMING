
import java.util.ArrayList;
import java.util.List;

public class Main {

    static volatile long checksum = 0;

    // Original approach: String concatenation
    static String generateUsingString(List<String> fields) {
        String ticket = "";

        for (String field : fields) {
            ticket = ticket + field;
        }

        return ticket;
    }

    // Redesigned approach: StringBuilder
    static String generateUsingBuilder(List<String> fields) {
        StringBuilder ticket = new StringBuilder();

        for (String field : fields) {
            ticket.append(field);
        }

        return ticket.toString();
    }

    public static void main(String[] args) {

        List<String> fields = new ArrayList<>();

        // Prepare identical input for both approaches
        for (int i = 1; i <= 300; i++) {
            fields.add("Field" + i + "-PassengerDetails;");
        }

        int repetitions = 100;

        // Warm-up
        for (int i = 0; i < 3; i++) {
            generateUsingString(fields);
            generateUsingBuilder(fields);
        }

        String originalResult = "";
        String redesignedResult = "";

        // Measure original implementation
        long start1 = System.nanoTime();

        for (int i = 0; i < repetitions; i++) {
            originalResult = generateUsingString(fields);
            checksum += originalResult.length();
        }

        long end1 = System.nanoTime();

        // Measure redesigned implementation
        long start2 = System.nanoTime();

        for (int i = 0; i < repetitions; i++) {
            redesignedResult = generateUsingBuilder(fields);
            checksum += redesignedResult.length();
        }

        long end2 = System.nanoTime();

        double originalTime =
            (end1 - start1) / 1_000_000.0;

        double redesignedTime =
            (end2 - start2) / 1_000_000.0;

        System.out.println("Fields per ticket: " + fields.size());
        System.out.println("Ticket generations: " + repetitions);

        System.out.println("\nOriginal String time: "
                           + originalTime + " ms");

        System.out.println("StringBuilder time: "
                           + redesignedTime + " ms");

        System.out.println("\nOutputs identical: "
                           + originalResult.equals(redesignedResult));

        System.out.println("Ticket length: "
                           + originalResult.length());
    }
}
