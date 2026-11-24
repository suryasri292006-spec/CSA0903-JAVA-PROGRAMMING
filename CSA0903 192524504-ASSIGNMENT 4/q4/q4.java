
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<String> points = new ArrayList<>();
    static int count = 0;

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== DRAWING PAD =====");
            System.out.println("1. Draw a Dot");
            System.out.println("2. Display All Dots");
            System.out.println("3. Repaint Window");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    drawDot();
                    break;

                case 2:
                    displayDots();
                    break;

                case 3:
                    repaintWindow();
                    break;

                case 4:
                    System.out.println("Closing Drawing Pad...");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

        } while (choice != 4);
    }

    static void drawDot() {

        System.out.print("Enter X Coordinate: ");
        int x = sc.nextInt();

        System.out.print("Enter Y Coordinate: ");
        int y = sc.nextInt();

        points.add("(" + x + ", " + y + ")");
        count++;

        System.out.println("Dot Drawn Successfully!");
        System.out.println("Clicks: " + count);
    }

    static void displayDots() {

        System.out.println("\n===== STORED DOTS =====");

        if (points.isEmpty()) {
            System.out.println("No Dots Available");
        } else {
            for (int i = 0; i < points.size(); i++) {
                System.out.println("Dot " + (i + 1) + ": " + points.get(i));
            }
        }

        System.out.println("Total Clicks: " + count);
    }

    static void repaintWindow() {

        System.out.println("\nRepainting Window...");
        System.out.println("Redrawing Stored Dots:");

        for (String point : points) {
            System.out.println("Dot at " + point);
        }

        System.out.println("Clicks: " + count);
        System.out.println("All Dots Restored Successfully!");
    }
}
