
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        double amount, converted;
        int choice;

        System.out.println("===== CURRENCY CONVERTER =====");

        System.out.print("Enter amount in INR: ");
        amount = sc.nextDouble();

        if (amount < 0) {
            System.out.println("Invalid amount");
        } else {

            System.out.println("\nSelect Target Currency:");
            System.out.println("1. USD");
            System.out.println("2. EUR");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            if (choice == 1) {
                converted = amount * 0.012;
                System.out.printf("Converted Amount: %.2f USD%n", converted);
            }
            else if (choice == 2) {
                converted = amount * 0.011;
                System.out.printf("Converted Amount: %.2f EUR%n", converted);
            }
            else {
                System.out.println("Invalid currency choice");
            }
        }

        sc.close();
    }
}
