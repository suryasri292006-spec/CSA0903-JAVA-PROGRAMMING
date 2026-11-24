
import java.util.*;

public class Main {

    static class BusPass {
        String passId, regNo, name, route;
        int amount;

        BusPass(String id, String reg, String n,
                String r, int a) {
            passId = id;
            regNo = reg;
            name = n;
            route = r;
            amount = a;
        }
    }

    static ArrayList<BusPass> records = new ArrayList<>();

    // Statement simulation
    static long statementInsert(int count) {
        long start = System.nanoTime();

        for (int i = 1; i <= count; i++) {
            records.add(new BusPass(
                "P" + i, "R" + i, "Passenger" + i,
                "Chennai", 500
            ));
        }

        return (System.nanoTime() - start) / 1000000;
    }

    // PreparedStatement simulation
    static long preparedInsert(int count) {
        long start = System.nanoTime();

        for (int i = 1; i <= count; i++) {
            BusPass pass = new BusPass(
                "P" + i, "R" + i, "Passenger" + i,
                "Chennai", 500
            );
            records.add(pass);
        }

        return (System.nanoTime() - start) / 1000000;
    }

    // Batch processing simulation
    static long batchInsert(int count) {
        long start = System.nanoTime();

        ArrayList<BusPass> batch = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            batch.add(new BusPass(
                "P" + i, "R" + i, "Passenger" + i,
                "Chennai", 500
            ));
        }

        records.addAll(batch);

        return (System.nanoTime() - start) / 1000000;
    }

    // Display sample records
    static void displayRecords() {
        System.out.println("\nBUS PASS RECORDS");

        for (int i = 0; i < Math.min(10, records.size()); i++) {
            BusPass p = records.get(i);

            System.out.println(
                p.passId + " | " + p.regNo + " | " +
                p.name + " | " + p.route + " | " + p.amount
            );
        }
    }

    // Transaction simulation
    static void transactionTest() {
        ArrayList<BusPass> transaction = new ArrayList<>();

        try {
            transaction.add(new BusPass(
                "P201", "R201", "Arun", "Chennai", 500
            ));

            transaction.add(new BusPass(
                "P202", "R202", "Divya", "Avadi", 600
            ));

            // Simulate an error halfway
            throw new Exception("Insertion Error");

        } catch (Exception e) {
            transaction.clear();
            System.out.println("\nTransaction Failed");
            System.out.println("Rollback completed");
        }

        System.out.println("Records saved in transaction: "
                + transaction.size());
    }

    public static void main(String[] args) {

        System.out.println("STATEMENT VS PREPAREDSTATEMENT VS BATCH");

        int[] sizes = {1000, 10000, 100000};

        for (int size : sizes) {

            records.clear();
            long t1 = statementInsert(size);

            records.clear();
            long t2 = preparedInsert(size);

            records.clear();
            long t3 = batchInsert(size);

            System.out.println("\nRecords: " + size);
            System.out.println("Statement Time: " + t1 + " ms");
            System.out.println("PreparedStatement Time: "
                    + t2 + " ms");
            System.out.println("Batch Processing Time: "
                    + t3 + " ms");
        }

        records.clear();

        // Insert 10 sample records
        batchInsert(10);

        displayRecords();

        System.out.println("\nTABLE METADATA");
        System.out.println("Number of Columns: 5");
        System.out.println("1. passId - VARCHAR");
        System.out.println("2. regNo - VARCHAR");
        System.out.println("3. passengerName - VARCHAR");
        System.out.println("4. route - VARCHAR");
        System.out.println("5. amount - INTEGER");

        transactionTest();

        System.out.println("\nProgram Completed Successfully");
    }
}
