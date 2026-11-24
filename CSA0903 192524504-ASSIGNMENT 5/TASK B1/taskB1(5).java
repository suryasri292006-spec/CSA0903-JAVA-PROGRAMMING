
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    static class PassStore {
        String file;

        PassStore(String file) {
            this.file = file;
        }

        // Add a bus pass
        void add(String id, String regNo, String name,
                 int route, double amount) throws IOException {

            try (BufferedWriter w = new BufferedWriter(
                    new FileWriter(file, true))) {

                w.write(id + "," + regNo + "," + name + ","
                        + route + "," + amount);
                w.newLine();
            }
        }

        // Find a pass using pass ID
        String[] find(String id) throws IOException {

            try (BufferedReader r = new BufferedReader(
                    new FileReader(file))) {

                String line;

                while ((line = r.readLine()) != null) {
                    String[] data = line.split(",", -1);

                    if (data.length != 5) {
                        continue;
                    }

                    if (data[0].equals(id)) {
                        return data;
                    }
                }
            }

            return null;
        }

        // List all passes on a route
        void list(int route) throws IOException {

            try (BufferedReader r = new BufferedReader(
                    new FileReader(file))) {

                String line;

                while ((line = r.readLine()) != null) {
                    String[] data = line.split(",", -1);

                    if (data.length == 5 &&
                        data[3].equals(String.valueOf(route))) {
                        System.out.println(Arrays.toString(data));
                    }
                }
            }
        }
    }

    // Count lines without buffering
    static long unbufferedCount(String file) throws IOException {

        long count = 0;

        try (FileInputStream in = new FileInputStream(file)) {
            int ch;

            while ((ch = in.read()) != -1) {
                if (ch == '\n') {
                    count++;
                }
            }
        }

        return count;
    }

    // Count lines using BufferedReader
    static long bufferedCount(String file) throws IOException {

        long count = 0;

        try (BufferedReader r = new BufferedReader(
                new FileReader(file))) {

            while (r.readLine() != null) {
                count++;
            }
        }

        return count;
    }

    // Measure line counting time
    static void measureBuffering(int mb) throws IOException {

        String file = "test" + mb + "MB.txt";
        long target = mb * 1024L * 1024L;

        try (BufferedWriter w = new BufferedWriter(
                new FileWriter(file))) {

            String line = "Attendance Record 1234567890\n";
            long written = 0;

            while (written < target) {
                w.write(line);
                written += line.length();
            }
        }

        long start = System.nanoTime();
        long a = unbufferedCount(file);
        long unbufferedTime = System.nanoTime() - start;

        start = System.nanoTime();
        long b = bufferedCount(file);
        long bufferedTime = System.nanoTime() - start;

        System.out.println("\nFile Size: " + mb + " MB");
        System.out.println("Unbuffered Lines: " + a);
        System.out.println("Buffered Lines: " + b);
        System.out.println("Unbuffered Time: "
                + unbufferedTime / 1000000.0 + " ms");
        System.out.println("Buffered Time: "
                + bufferedTime / 1000000.0 + " ms");

        new File(file).delete();
    }

    // Measure search time as records increase
    static void measureGrowth() throws IOException {

        int[] sizes = {1000, 10000, 50000, 100000};

        System.out.println("\n--- Search Growth Measurement ---");

        for (int size : sizes) {

            String file = "growth.txt";

            try (BufferedWriter w = new BufferedWriter(
                    new FileWriter(file))) {

                for (int i = 1; i <= size; i++) {
                    w.write("P" + i + ",R" + i + ",Student"
                            + i + ",12,4500");
                    w.newLine();
                }
            }

            PassStore store = new PassStore(file);

            long start = System.nanoTime();
            store.find("P" + size);
            long time = System.nanoTime() - start;

            System.out.println(size + " records: "
                    + time / 1000000.0 + " ms");

            new File(file).delete();
        }
    }

    public static void main(String[] args) throws IOException {

        String file = "passes.txt";
        new File(file).delete();

        PassStore store = new PassStore(file);

        System.out.println("--- ADD BUS PASSES ---");

        store.add("P1001", "192311001", "Arun", 12, 4500);
        store.add("P1002", "192311002", "Divya", 7, 3800);
        store.add("P1003", "192311003", "Kumar", 12, 4200);

        // Add a damaged record for testing
        Files.writeString(Paths.get(file),
                "P1004,DAMAGED LINE\n",
                StandardOpenOption.APPEND);

        System.out.println("Passes added successfully.");

        System.out.println("\n--- FIND EXISTING PASS ---");
        System.out.println(Arrays.toString(store.find("P1003")));

        System.out.println("\n--- FIND NON-EXISTING PASS ---");
        System.out.println(Arrays.toString(store.find("P9999")));

        System.out.println("\n--- LIST ROUTE 12 ---");
        store.list(12);

        System.out.println("\n--- BUFFERING PERFORMANCE ---");

        measureBuffering(1);
        measureBuffering(10);
        measureBuffering(50);

        measureGrowth();

        new File(file).delete();

        System.out.println("\nProgram completed successfully.");
    }
}

