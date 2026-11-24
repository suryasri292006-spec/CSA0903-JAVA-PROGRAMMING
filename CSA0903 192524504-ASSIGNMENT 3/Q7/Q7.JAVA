
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

class ProcessingResult {

    String fileName;
    boolean success;
    String message;

    ProcessingResult(String fileName, boolean success, String message) {
        this.fileName = fileName;
        this.success = success;
        this.message = message;
    }
}

class ScoreProcessor implements Callable<ProcessingResult> {

    private final String fileName;

    ScoreProcessor(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public ProcessingResult call() {

        try {
            File file = new File(fileName);

            Scanner sc = new Scanner(file);

            if (!sc.hasNextLine()) {
                sc.close();
                throw new IllegalArgumentException("Empty score file");
            }

            String line = sc.nextLine();
            sc.close();

            String[] data = line.split(",");

            if (data.length != 2) {
                throw new IllegalArgumentException("Corrupt file format");
            }

            String studentId = data[0];
            int marks = Integer.parseInt(data[1]);

            if (marks < 0 || marks > 100) {
                throw new IllegalArgumentException("Invalid marks");
            }

            String result = marks >= 40 ? "PASS" : "FAIL";

            return new ProcessingResult(
                    fileName, true,
                    studentId + " : " + marks + " : " + result);

        } catch (FileNotFoundException e) {

            return new ProcessingResult(
                    fileName, false, "Missing file");

        } catch (NumberFormatException e) {

            return new ProcessingResult(
                    fileName, false, "Invalid marks format");

        } catch (IllegalArgumentException e) {

            return new ProcessingResult(
                    fileName, false, e.getMessage());

        } catch (IOException e) {

            return new ProcessingResult(
                    fileName, false, "File reading error");
        }
    }
}

public class Main {

    static final int TOTAL_FILES = 10000;
    static final String FOLDER = "score_files";

    // Create test files and inject errors
    static void createFiles() throws IOException {

        Files.createDirectories(Paths.get(FOLDER));

        for (int i = 1; i <= TOTAL_FILES; i++) {

            String fileName = FOLDER + "/student" + i + ".txt";

            // Skip 10 files to simulate missing files
            if (i >= 9991 && i <= 10000) {
                continue;
            }

            String content;

            // Insert 10 corrupt files
            if (i >= 9981 && i <= 9990) {
                content = "INVALID_DATA";
            } else {
                int marks = (i % 101);
                content = "STU" + i + "," + marks;
            }

            Files.write(
                    Paths.get(fileName),
                    content.getBytes()
            );
        }
    }

    // Process files using different thread pool sizes
    static void processFiles(int poolSize) {

        ExecutorService executor =
                Executors.newFixedThreadPool(poolSize);

        ConcurrentLinkedQueue<ProcessingResult> results =
                new ConcurrentLinkedQueue<>();

        List<Future<ProcessingResult>> futures =
                new ArrayList<>();

        long startTime = System.nanoTime();

        // Submit all 10,000 tasks
        for (int i = 1; i <= TOTAL_FILES; i++) {

            String fileName =
                    FOLDER + "/student" + i + ".txt";

            Callable<ProcessingResult> task =
                    new ScoreProcessor(fileName);

            futures.add(executor.submit(task));
        }

        // Collect all task results
        for (Future<ProcessingResult> future : futures) {

            try {
                ProcessingResult result = future.get();
                results.add(result);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                System.out.println("Processing interrupted");
                break;

            } catch (ExecutionException e) {

                results.add(new ProcessingResult(
                        "Unknown file", false,
                        "Unexpected processing error"));
            }
        }

        executor.shutdown();

        long endTime = System.nanoTime();

        int success = 0;
        int failure = 0;
        int missing = 0;
        int corrupt = 0;

        for (ProcessingResult result : results) {

            if (result.success) {
                success++;
            } else {
                failure++;

                if (result.message.equals("Missing file")) {
                    missing++;
                } else {
                    corrupt++;
                }
            }
        }

        double timeTaken =
                (endTime - startTime) / 1_000_000.0;

        System.out.println("\nThread Pool Size: " + poolSize);
        System.out.println("Total files: " + TOTAL_FILES);
        System.out.println("Successful files: " + success);
        System.out.println("Failed files: " + failure);
        System.out.println("Missing files: " + missing);
        System.out.println("Corrupt files: " + corrupt);

        System.out.printf("Time taken: %.2f ms%n", timeTaken);

        System.out.println("------------------------------");
    }

    public static void main(String[] args) {

        try {

            createFiles();

            System.out.println("SEMESTER RESULT PROCESSING");
            System.out.println("==========================");

            processFiles(1);
            processFiles(2);
            processFiles(4);
            processFiles(8);

            System.out.println("All processing completed.");

        } catch (IOException e) {

            System.out.println(
                    "File creation error: " + e.getMessage());
        }
    }
}
