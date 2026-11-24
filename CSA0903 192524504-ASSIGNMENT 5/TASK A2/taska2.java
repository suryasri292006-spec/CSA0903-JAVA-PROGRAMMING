
import java.io.*;

public class Main {

    public static void main(String[] args) {

        String fileName = "attendance.txt";

        // Create sample attendance file
        try {
            BufferedWriter bw = new BufferedWriter(
                new FileWriter(fileName));

            for (int i = 1; i <= 10000; i++) {
                bw.write("Student Attendance Record " + i);
                bw.newLine();
            }

            bw.close();

            System.out.println("File created successfully!");

        } catch (IOException e) {
            System.out.println("File creation error: " + e.getMessage());
            return;
        }

        // Unbuffered Reading
        long start1 = System.nanoTime();
        int count1 = 0;

        try {
            FileInputStream fis = new FileInputStream(fileName);
            int data;

            while ((data = fis.read()) != -1) {
                if (data == '\n') {
                    count1++;
                }
            }

            fis.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        long end1 = System.nanoTime();

        // Buffered Reading
        long start2 = System.nanoTime();
        int count2 = 0;

        try {
            BufferedReader br = new BufferedReader(
                new FileReader(fileName));

            String line;

            while ((line = br.readLine()) != null) {
                count2++;
            }

            br.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        long end2 = System.nanoTime();

        // Display Results
        System.out.println("\n===== ATTENDANCE FILE REPORT =====");

        System.out.println("Unbuffered Line Count: " + count1);
        System.out.println("Buffered Line Count: " + count2);

        System.out.println("Unbuffered Time: "
                + (end1 - start1) + " nanoseconds");

        System.out.println("Buffered Time: "
                + (end2 - start2) + " nanoseconds");
    }
}
