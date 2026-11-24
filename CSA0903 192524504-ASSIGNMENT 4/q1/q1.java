import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static String regNo = "";
    static double amount = 0;
    static boolean paid = false;

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== STUDENT FEE PAYMENT =====");
            System.out.println("1. Pay Fee");
            System.out.println("2. Clear Details");
            System.out.println("3. View Receipt");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    payFee();
                    break;

                case 2:
                    clearDetails();
                    break;

                case 3:
                    viewReceipt();
                    break;

                case 4:
                    System.out.println("Thank You!");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while (choice != 4);
    }

    static void payFee() {

        System.out.print("Enter Registration Number: ");
        regNo = sc.nextLine();

        System.out.print("Enter Fee Amount: ");

        try {
            amount = Double.parseDouble(sc.nextLine());

            if (regNo.isEmpty() || amount <= 0) {
                System.out.println("Enter Valid Details");
                paid = false;
            } else {
                paid = true;
                System.out.println("Payment Successful!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid Amount");
            paid = false;
        }
    }

    static void clearDetails() {

        regNo = "";
        amount = 0;
        paid = false;

        System.out.println("Details Cleared Successfully");
    }

    static void viewReceipt() {

        if (paid) {
            System.out.println("\n===== PAYMENT RECEIPT =====");
            System.out.println("Registration Number: " + regNo);
            System.out.println("Fee Amount: Rs. " + amount);
            System.out.println("Status: Payment Successful");
        } else {
            System.out.println("No Payment Receipt Available");
        }
    }
}
