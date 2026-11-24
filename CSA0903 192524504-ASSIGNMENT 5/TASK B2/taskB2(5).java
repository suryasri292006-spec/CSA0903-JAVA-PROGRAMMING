
import java.io.*;

public class Main {

    // Simulates an unsafe login using SQL concatenation
    static boolean unsafeLogin(String id, String password) {
        String query = "SELECT * FROM users WHERE id='" + id
                + "' AND password='" + password + "'";

        System.out.println("Unsafe SQL Query:");
        System.out.println(query);

        // Demonstrates the SQL injection input
        if (password.equals("' OR '1'='1")) {
            return true;
        }

        return id.equals("101") && password.equals("pass123");
    }

    // Simulates safe login using PreparedStatement
    static boolean safeLogin(String id, String password) {
        // Parameter values are treated as data, not SQL commands
        return id.equals("101") && password.equals("pass123");
    }

    // Checks damaged bus-pass records
    static void checkFile() {
        String fileName = "buspass.txt";

        try {
            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(fileName));

            writer.write("P101,REG01,Arun,Chennai,500");
            writer.newLine();

            writer.write("P102,REG02,Divya,Chennai");
            writer.newLine();

            writer.write("P103,REG03,Kumar,Chennai,abc");
            writer.newLine();

            writer.newLine();

            writer.write("P104,REG04,Anu,Avadi,600");
            writer.newLine();

            writer.close();

            System.out.println("\nBus Pass File Validation:");

            BufferedReader reader =
                    new BufferedReader(new FileReader(fileName));

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    System.out.println("Line " + lineNumber
                            + ": Empty line - Skipped");
                    continue;
                }

                String[] fields = line.split(",", -1);

                if (fields.length != 5) {
                    System.out.println("Line " + lineNumber
                            + ": Wrong number of fields - Skipped");
                    continue;
                }

                try {
                    int amount = Integer.parseInt(fields[4]);

                    System.out.println("Line " + lineNumber
                            + ": Valid record, Amount = " + amount);

                } catch (NumberFormatException e) {
                    System.out.println("Line " + lineNumber
                            + ": Invalid amount - Skipped");
                }
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        String id = "101";
        String attackPassword = "' OR '1'='1";

        System.out.println("UNSAFE LOGIN TEST");

        if (unsafeLogin(id, attackPassword)) {
            System.out.println("Unsafe Login: Access Granted");
        } else {
            System.out.println("Unsafe Login: Access Denied");
        }

        System.out.println("\nSAFE LOGIN TEST");

        if (safeLogin(id, attackPassword)) {
            System.out.println("Safe Login: Access Granted");
        } else {
            System.out.println("Safe Login: Access Denied");
        }

        checkFile();
    }
}
