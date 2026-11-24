
import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static String[][] books = {
        {"Java Programming", "Computer Science", "James Gosling"},
        {"Python Basics", "Computer Science", "John Smith"},
        {"Data Structures", "Computer Science", "Mark Allen"},
        {"Engineering Mathematics", "Mathematics", "B.S. Grewal"},
        {"Applied Physics", "Physics", "R.K. Gaur"},
        {"English Grammar", "English", "Wren and Martin"}
    };

    static ArrayList<String[]> results = new ArrayList<>();

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== CAMPUS LIBRARY SEARCH =====");
            System.out.println("1. Search Books");
            System.out.println("2. View Next Page");
            System.out.println("3. View Previous Page");
            System.out.println("4. Reserve Book");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    searchBooks();
                    break;

                case 2:
                    System.out.println("Use Search Books to view results.");
                    break;

                case 3:
                    System.out.println("Use Search Books to view results.");
                    break;

                case 4:
                    reserveBook();
                    break;

                case 5:
                    System.out.println("Thank you for using Library Search!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 5);
    }

    static void searchBooks() {

        results.clear();

        System.out.print("Enter Book Name: ");
        String name = sc.nextLine().toLowerCase();

        System.out.println("Select Category:");
        System.out.println("1. All");
        System.out.println("2. Computer Science");
        System.out.println("3. Mathematics");
        System.out.println("4. Physics");
        System.out.println("5. English");
        System.out.print("Enter category: ");

        int categoryChoice = sc.nextInt();
        sc.nextLine();

        String category = "";

        switch (categoryChoice) {
            case 1: category = "All"; break;
            case 2: category = "Computer Science"; break;
            case 3: category = "Mathematics"; break;
            case 4: category = "Physics"; break;
            case 5: category = "English"; break;
            default:
                System.out.println("Invalid category!");
                return;
        }

        for (String[] book : books) {

            boolean nameMatch =
                book[0].toLowerCase().contains(name);

            boolean categoryMatch =
                category.equals("All") ||
                book[1].equals(category);

            if (nameMatch && categoryMatch) {
                results.add(book);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No books found!");
        } else {
            System.out.println("\n===== SEARCH RESULTS =====");

            for (int i = 0; i < results.size(); i++) {
                System.out.println("\nBook Number: " + (i + 1));
                System.out.println("Book Name: " + results.get(i)[0]);
                System.out.println("Category: " + results.get(i)[1]);
                System.out.println("Author: " + results.get(i)[2]);
            }
        }
    }

    static void reserveBook() {

        if (results.isEmpty()) {
            System.out.println("Please search for a book first.");
            return;
        }

        System.out.print("Enter Book Number to Reserve: ");
        int number = sc.nextInt();
        sc.nextLine();

        if (number >= 1 && number <= results.size()) {
            System.out.println("Book Reserved Successfully!");
            System.out.println("Reserved Book: " +
                results.get(number - 1)[0]);
        } else {
            System.out.println("Invalid Book Number!");
        }
    }
}
