
public class Main {

    static final int RUNS = 5;

    // 1. Using String
    static String usingString(int fields) {
        String ticket = "";

        for (int i = 1; i <= fields; i++) {
            ticket = ticket + "Field" + i + " ";
        }

        return ticket;
    }

    // 2. Using StringBuilder
    static String usingStringBuilder(int fields) {
        StringBuilder ticket = new StringBuilder();

        for (int i = 1; i <= fields; i++) {
            ticket.append("Field").append(i).append(" ");
        }

        return ticket.toString();
    }

    // 3. Using StringBuffer
    static String usingStringBuffer(int fields) {
        StringBuffer ticket = new StringBuffer();

        for (int i = 1; i <= fields; i++) {
            ticket.append("Field").append(i).append(" ");
        }

        return ticket.toString();
    }

    // Generate multiple tickets
    static void generateTickets(int tickets, int fields, int method) {

        for (int i = 0; i < tickets; i++) {

            if (method == 1) {
                usingString(fields);
            } 
            else if (method == 2) {
                usingStringBuilder(fields);
            } 
            else {
                usingStringBuffer(fields);
            }
        }
    }

    // Measure execution time
    static double measureTime(int tickets, int fields, int method) {

        long totalTime = 0;

        for (int i = 0; i < RUNS; i++) {

            long start = System.nanoTime();

            generateTickets(tickets, fields, method);

            long end = System.nanoTime();

            totalTime += (end - start);
        }

        return totalTime / (double) RUNS / 1_000_000;
    }

    // Measure approximate memory usage
    static long memoryUsed() {
        Runtime runtime = Runtime.getRuntime();

        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static void main(String[] args) {

        int[][] workloads = {
            {1, 6},          // Workload 1: Short ticket
            {10000, 50},     // Workload 2: Large workload
            {1000, 200}      // Workload 3: Many fields
        };

        String[] methods = {
            "String (+)",
            "StringBuilder",
            "StringBuffer"
        };

        System.out.println("RAILWAY E-TICKET GENERATION");
        System.out.println("------------------------------------------");

        for (int w = 0; w < workloads.length; w++) {

            int tickets = workloads[w][0];
            int fields = workloads[w][1];

            System.out.println("\nWorkload " + (w + 1));
            System.out.println("Tickets: " + tickets);
            System.out.println("Fields per ticket: " + fields);

            for (int m = 0; m < 3; m++) {

                // Warm-up
                for (int j = 0; j < 3; j++) {
                    generateTickets(tickets, fields, m + 1);
                }

                System.gc();

                long beforeMemory = memoryUsed();

                double time = measureTime(tickets, fields, m + 1);

                long afterMemory = memoryUsed();

                System.out.printf(
                    "%-15s : %.3f ms | Memory change: %d bytes%n",
                    methods[m],
                    time,
                    afterMemory - beforeMemory
                );
            }
        }

        // Display sample ticket
        System.out.println("\n------------------------------------------");
        System.out.println("TICKET SAMPLE:");

        System.out.println(usingStringBuilder(6));

        // Design decision
        System.out.println("\nDESIGN DECISION:");
        System.out.println("Per-ticket text : StringBuilder");
        System.out.println("Shared log      : StringBuffer");

        // Complexity analysis
        System.out.println("\nCOMPLEXITY ANALYSIS:");
        System.out.println("String (+)      : O(n^2) character copying");
        System.out.println("StringBuilder   : O(n) amortized");
        System.out.println("StringBuffer    : O(n) amortized");

        System.out.println("\nPROGRAM EXECUTED SUCCESSFULLY!");
    }
}
