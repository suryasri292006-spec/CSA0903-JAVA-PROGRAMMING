
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static Map<String, List<String>> doctors = new HashMap<>();

    static String patientID = "";
    static String department = "";
    static String doctor = "";
    static String timeSlot = "";

    static boolean bookingInProgress = false;
    static boolean booked = false;

    public static void main(String[] args) {

        doctors.put("Cardiology",
            Arrays.asList("Dr. Kumar", "Dr. Ravi"));

        doctors.put("Neurology",
            Arrays.asList("Dr. Priya", "Dr. Meena"));

        doctors.put("Orthopedics",
            Arrays.asList("Dr. Arun", "Dr. Suresh"));

        int choice;

        do {
            System.out.println("\n===== HOSPITAL APPOINTMENT DESK =====");
            System.out.println("1. Book Appointment");
            System.out.println("2. View Appointment");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    bookAppointment();
                    break;

                case 2:
                    viewAppointment();
                    break;

                case 3:
                    if (bookingInProgress) {
                        System.out.print(
                            "Booking in progress. Exit? (yes/no): ");
                        String answer = sc.nextLine();

                        if (answer.equalsIgnoreCase("yes")) {
                            System.out.println("Closing Application");
                            choice = 3;
                        } else {
                            choice = 0;
                        }
                    } else {
                        System.out.println("Thank You!");
                    }
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while (choice != 3 || bookingInProgress);
    }

    static void bookAppointment() {

        bookingInProgress = true;
        booked = false;

        System.out.print("Enter Patient ID: ");
        patientID = sc.nextLine();

        if (patientID.trim().isEmpty()) {
            System.out.println("Patient ID cannot be empty");
            bookingInProgress = false;
            return;
        }

        System.out.println("\nSelect Department:");
        System.out.println("1. Cardiology");
        System.out.println("2. Neurology");
        System.out.println("3. Orthopedics");

        System.out.print("Enter Department Choice: ");
        int deptChoice = sc.nextInt();
        sc.nextLine();

        switch (deptChoice) {
            case 1:
                department = "Cardiology";
                break;

            case 2:
                department = "Neurology";
                break;

            case 3:
                department = "Orthopedics";
                break;

            default:
                System.out.println("Invalid Department");
                bookingInProgress = false;
                return;
        }

        // Refresh doctor list
        List<String> doctorList = doctors.get(department);

        System.out.println("\nAvailable Doctors:");

        for (int i = 0; i < doctorList.size(); i++) {
            System.out.println((i + 1) + ". " + doctorList.get(i));
        }

        System.out.print("Select Doctor: ");
        int doctorChoice = sc.nextInt();
        sc.nextLine();

        if (doctorChoice < 1 ||
            doctorChoice > doctorList.size()) {

            System.out.println("Invalid Doctor Selection");
            bookingInProgress = false;
            return;
        }

        doctor = doctorList.get(doctorChoice - 1);

        System.out.println("\nAvailable Time Slots:");
        System.out.println("1. 10:00 AM");
        System.out.println("2. 11:00 AM");
        System.out.println("3. 02:00 PM");

        System.out.print("Select Time Slot: ");
        int slotChoice = sc.nextInt();
        sc.nextLine();

        switch (slotChoice) {
            case 1:
                timeSlot = "10:00 AM";
                break;

            case 2:
                timeSlot = "11:00 AM";
                break;

            case 3:
                timeSlot = "02:00 PM";
                break;

            default:
                System.out.println("Invalid Time Slot");
                bookingInProgress = false;
                return;
        }

        System.out.println("\nConfirm Appointment? (yes/no)");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("yes")) {

            booked = true;

            System.out.println("\nAppointment Booked Successfully!");

        } else {
            System.out.println("Appointment Cancelled");
        }

        bookingInProgress = false;
    }

    static void viewAppointment() {

        if (booked) {

            System.out.println("\n===== APPOINTMENT DETAILS =====");
            System.out.println("Patient ID: " + patientID);
            System.out.println("Department: " + department);
            System.out.println("Doctor: " + doctor);
            System.out.println("Time Slot: " + timeSlot);
            System.out.println("Status: Confirmed");

        } else {
            System.out.println("No Appointment Available");
        }
    }
}
