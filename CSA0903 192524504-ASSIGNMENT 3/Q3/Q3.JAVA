
import java.io.*;
import java.util.*;

class TrackingReader extends BufferedReader {

    static int openFiles = 0;
    static int maxOpenFiles = 0;

    TrackingReader(String fileName) throws IOException {
        super(new FileReader(fileName));
        openFiles++;
        maxOpenFiles = Math.max(maxOpenFiles, openFiles);
    }

    @Override
    public void close() throws IOException {
        if (openFiles > 0) {
            openFiles--;
        }
        super.close();
    }
}

public class Main {

    static void createFiles() throws IOException {

        try (PrintWriter p1 = new PrintWriter("store1.txt");
             PrintWriter p2 = new PrintWriter("store2.txt");
             PrintWriter p3 = new PrintWriter("store3.txt")) {

            p1.println("Store 1 Sales: 1000");
            p2.println("Store 2 Sales: 2000");
            p3.println("Store 3 Sales: 3000");
        }
    }

    // VERSION 1: Manual closing using finally
    static void manualVersion() {

        List<TrackingReader> readers = new ArrayList<>();

        try {
            // Open all store files
            readers.add(new TrackingReader("store1.txt"));
            readers.add(new TrackingReader("store2.txt"));
            readers.add(new TrackingReader("store3.txt"));

            System.out.println("\nManual Version:");

            for (TrackingReader reader : readers) {

                String line = reader.readLine();

                if (line == null) {
                    continue;
                }

                if (line.contains("2000")) {
                    throw new IOException("Error reading store2.txt");
                }

                System.out.println(line);
            }

        } catch (IOException e) {

            System.out.println("Error: " + e.getMessage());

        } finally {

            // Close every opened file
            for (TrackingReader reader : readers) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Close error: " + e.getMessage());
                }
            }

            System.out.println("Manual open files after closing: "
                    + TrackingReader.openFiles);
        }
    }

    // VERSION 2: Try-with-resources
    static void tryWithResourcesVersion() {

        System.out.println("\nTry-With-Resources Version:");

        try {
            String[] files = {
                "store1.txt",
                "store2.txt",
                "store3.txt"
            };

            for (String file : files) {

                try (TrackingReader reader =
                             new TrackingReader(file)) {

                    String line = reader.readLine();

                    if (line == null) {
                        continue;
                    }

                    if (line.contains("2000")) {
                        throw new IOException(
                            "Error reading " + file);
                    }

                    System.out.println(line);
                }
            }

        } catch (IOException e) {

            System.out.println("Error: " + e.getMessage());

        }

        System.out.println("Try-with-resources open files after closing: "
                + TrackingReader.openFiles);
    }

    public static void main(String[] args) {

        try {
            createFiles();

            manualVersion();

            tryWithResourcesVersion();

        } catch (IOException e) {

            System.out.println("File creation error: " + e.getMessage());
        }
    }
}
