
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static class Registration {
        String studentId;
        String studentName;
        String eventName;

        Registration(String id, String name, String event) {
            studentId = id;
            studentName = name;
            eventName = event;
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ArrayList<Registration> registrations = new ArrayList<>();

        int choice;

        do {
            System.out.println("\n===== CAMPUS EVENT REGISTRATION =====");
            System.out.println("1. Register Student");
            System.out.println("2. View Registrations");
            System.out.println("3. Filter by Event");
            System.out.println("4. Export Registration List");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Student ID: ");
                    String id = sc.nextLine();

                    System.out.print("Enter Student Name: ");
                    String name = sc.nextLine();

                    System.out.println("Select Event:");
                    System.out.println("1. Coding Competition");
                    System.out.println("2. Cultural Fest");
                    System.out.println("3. Sports Event");

                    System.out.print("Enter event choice: ");
                    int eventChoice = sc.nextInt();
                    sc.nextLine();

                    String event = "";

                    if (eventChoice == 1) {
                        event = "Coding Competition";
                    } else if (eventChoice == 2) {
                        event = "Cultural Fest";
                    } else if (eventChoice == 3) {
                        event = "Sports Event";
                    } else {
                        System.out.println("Invalid event choice!");
                        break;
                    }

                    if (id.isEmpty() || name.trim().isEmpty()) {
                        System.out.println("Please enter all details.");
                        break;
                    }

                    boolean duplicate = false;

                    for (Registration r : registrations) {
                        if (r.studentId.equalsIgnoreCase(id)
                                && r.eventName.equals(event)) {
                            duplicate = true;
                            break;
                        }
                    }

                    if (duplicate) {
                        System.out.println("Duplicate registration not allowed!");
                    } else {
                        registrations.add(
                            new Registration(id, name, event)
                        );
                        System.out.println("Registration successful!");
                    }
                    break;

                case 2:
                    System.out.println("\n===== ALL REGISTRATIONS =====");

                    if (registrations.isEmpty()) {
                        System.out.println("No registrations found.");
                    } else {
                        for (Registration r : registrations) {
                            System.out.println(
                                "ID: " + r.studentId
                                + " | Name: " + r.studentName
                                + " | Event: " + r.eventName
                            );
                        }
                    }
                    break;

                case 3:
                    System.out.println("\nSelect Event to Filter:");
                    System.out.println("1. Coding Competition");
                    System.out.println("2. Cultural Fest");
                    System.out.println("3. Sports Event");

                    System.out.print("Enter choice: ");
                    int filterChoice = sc.nextInt();
                    sc.nextLine();

                    String filterEvent = "";

                    if (filterChoice == 1) {
                        filterEvent = "Coding Competition";
                    } else if (filterChoice == 2) {
                        filterEvent = "Cultural Fest";
                    } else if (filterChoice == 3) {
                        filterEvent = "Sports Event";
                    } else {
                        System.out.println("Invalid choice!");
                        break;
                    }

                    System.out.println("\nFiltered Registrations:");

                    boolean found = false;

                    for (Registration r : registrations) {
                        if (r.eventName.equals(filterEvent)) {
                            System.out.println(
                                r.studentId + " - "
                                + r.studentName + " - "
                                + r.eventName
                            );
                            found = true;
                        }
                    }

                    if (!found) {
                        System.out.println("No records found.");
                    }
                    break;

                case 4:
                    System.out.println("\n===== EXPORT REGISTRATION LIST =====");

                    if (registrations.isEmpty()) {
                        System.out.println("No data available to export.");
                    } else {
                        for (Registration r : registrations) {
                            System.out.println(
                                r.studentId + ","
                                + r.studentName + ","
                                + r.eventName
                            );
                        }
                        System.out.println("Registration list displayed successfully.");
                    }
                    break;

                case 5:
                    System.out.println("Thank you! Application closed.");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 5);

        sc.close();
    }
}
