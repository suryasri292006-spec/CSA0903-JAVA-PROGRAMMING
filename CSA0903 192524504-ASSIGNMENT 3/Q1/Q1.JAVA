
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;

class InsufficientBalanceException extends Exception {
    InsufficientBalanceException(String message) {
        super(message);
    }
}

public class Main {

    static void withdraw(int balance, int amount)
            throws InsufficientBalanceException {

        if (amount < 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount cannot be negative");
        }

        if (amount == 0) {
            throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero");
        }

        if (amount > balance) {
            throw new InsufficientBalanceException(
                "Insufficient account balance");
        }

        System.out.println("Please collect your cash: Rs." + amount);
        System.out.println("Withdrawal successful!");
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int balance = 10000;

        try {
            // Simulate card-reader file
            try (FileReader card = new FileReader("card.txt")) {
                System.out.println("Card reader file accessed successfully.");
            }

            System.out.print("Enter account number: ");
            int accountNumber = sc.nextInt();

            if (accountNumber <= 0) {
                throw new IllegalArgumentException(
                    "Invalid account number");
            }

            System.out.print("Enter withdrawal amount: ");
            int amount = sc.nextInt();

            withdraw(balance, amount);

        } catch (InputMismatchException e) {
            System.out.println(
                "Error: Please enter numeric values only.");

        } catch (IOException e) {
            System.out.println(
                "Error: Card-reader file is missing or cannot be accessed.");

        } catch (InsufficientBalanceException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());

        } finally {
            System.out.println(
                "ATM transaction process completed.");
            sc.close();
        }
    }
}
