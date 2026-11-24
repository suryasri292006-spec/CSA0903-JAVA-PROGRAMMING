
import java.io.*;
import java.util.*;

public class Main {

    static String fileName = "passes.txt";

    // Add a new bus pass
    static void addPass(String id, String regNo, String name,
                        int route, String stop, String date,
                        double amount) throws IOException {

        BufferedWriter bw = new BufferedWriter(
            new FileWriter(fileName, true));

        bw.write(id + "," + regNo + "," + name + ","
                + route + "," + stop + "," + date + "," + amount);

        bw.newLine();
        bw.close();

        System.out.println("Pass added successfully!");
    }

    // Find pass by ID
    static void findPass(String searchId) throws IOException {

        BufferedReader br = new BufferedReader(
            new FileReader(fileName));

        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",", -1);

            if (data.length != 7) {
                System.out.println("Damaged line skipped: " + line);
                continue;
            }

            if (data[0].equals(searchId)) {
                System.out.println("Pass Details:");
                System.out.println(Arrays.toString(data));
                found = true;
                break;
            }
        }

        br.close();

        if (!found) {
            System.out.println("Pass not found!");
        }
    }

    // List passes by route
    static void listByRoute(int route) throws IOException {

        BufferedReader br = new BufferedReader(
            new FileReader(fileName));

        String line;
        boolean found = false;

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",", -1);

            if (data.length != 7) {
                continue;
            }

            if (Integer.parseInt(data[3]) == route) {
                System.out.println(Arrays.toString(data));
                found = true;
            }
        }

        br.close();

        if (!found) {
            System.out.println("No passes found for this route.");
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== BUS PASS MANAGEMENT =====");
            System.out.println("1. Add Pass");
            System.out.println("2. Find Pass");
            System.out.println("3. List Passes by Route");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Pass ID: ");
                    String id = sc.nextLine();

                    System.out.print("Register Number: ");
                    String reg = sc.nextLine();

                    System.out.print("Student Name: ");
                    String name = sc.nextLine();

                    System.out.print("Route Number: ");
                    int route = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Boarding Stop: ");
                    String stop = sc.nextLine();

                    System.out.print("Validity Date: ");
                    String date = sc.nextLine();

                    System.out.print("Amount Paid: ");
                    double amount = sc.nextDouble();
                    sc.nextLine();

                    addPass(id, reg, name, route, stop, date, amount);
                    break;

                case 2:
                    System.out.print("Enter Pass ID: ");
                    String searchId = sc.nextLine();
                    findPass(searchId);
                    break;

                case 3:
                    System.out.print("Enter Route Number: ");
                    int searchRoute = sc.nextInt();
                    sc.nextLine();
                    listByRoute(searchRoute);
                    break;

                case 4:
                    System.out.println("Program closed.");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 4);

        sc.close();
    }
}
