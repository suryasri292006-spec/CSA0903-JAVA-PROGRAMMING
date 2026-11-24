
import java.util.*;

class Medicine {
    String code;
    String name;
    int quantity;
    double price;

    Medicine(String code, String name, int quantity, double price) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return code + " | " + name + " | Stock: "
                + quantity + " | Price: " + price;
    }
}

public class Main {

    static final int LOOKUPS = 100000;
    static volatile long result = 0;

    // Generate medicine code
    static String generateCode(int number) {
        return String.format("MED%05d", number);
    }

    // Create medicine
    static Medicine createMedicine(int number) {
        return new Medicine(
                generateCode(number),
                "Medicine" + number,
                number * 2,
                50.0 + number
        );
    }

    // Measure lookup time
    static double measureLookup(Map<String, Medicine> map, int size) {

        long total = 0;

        long start = System.nanoTime();

        for (int i = 0; i < LOOKUPS; i++) {
            String code = generateCode((i % size) + 1);

            Medicine medicine = map.get(code);

            if (medicine != null) {
                total += medicine.quantity;
            }
        }

        long end = System.nanoTime();

        result = total;

        return (end - start) / 1_000_000.0;
    }

    // Generate report in medicine-code order
    static List<Medicine> codeOrderReport(
            Map<String, Medicine> map) {

        List<Medicine> report = new ArrayList<>(map.values());

        report.sort(Comparator.comparing(m -> m.code));

        return report;
    }

    // Generate insertion-order report using saved keys
    static List<Medicine> insertionOrderReport(
            Map<String, Medicine> map,
            List<String> insertionKeys) {

        List<Medicine> report = new ArrayList<>();

        for (String key : insertionKeys) {
            report.add(map.get(key));
        }

        return report;
    }

    // Measure report generation
    static double measureCodeReport(Map<String, Medicine> map) {

        long start = System.nanoTime();

        List<Medicine> report = codeOrderReport(map);

        long end = System.nanoTime();

        result += report.size();

        return (end - start) / 1_000_000.0;
    }

    static double measureInsertionReport(
            Map<String, Medicine> map,
            List<String> keys) {

        long start = System.nanoTime();

        List<Medicine> report = insertionOrderReport(map, keys);

        long end = System.nanoTime();

        result += report.size();

        return (end - start) / 1_000_000.0;
    }

    // Measure full map iteration
    static double measureIteration(Map<String, Medicine> map) {

        long start = System.nanoTime();

        long count = 0;

        for (Medicine m : map.values()) {
            count += m.quantity;
        }

        long end = System.nanoTime();

        result += count;

        return (end - start) / 1_000_000.0;
    }

    // Check code order
    static boolean isCodeSorted(List<Medicine> medicines) {

        for (int i = 1; i < medicines.size(); i++) {

            if (medicines.get(i - 1).code.compareTo(
                    medicines.get(i).code) > 0) {
                return false;
            }
        }

        return true;
    }

    // Check insertion order
    static boolean isInsertionOrder(
            List<Medicine> medicines,
            List<String> keys) {

        if (medicines.size() != keys.size()) {
            return false;
        }

        for (int i = 0; i < medicines.size(); i++) {

            if (!medicines.get(i).code.equals(keys.get(i))) {
                return false;
            }
        }

        return true;
    }

    // Run experiments
    static void runExperiment(int size) {

        Map<String, Medicine> hashMap = new HashMap<>();
        Map<String, Medicine> treeMap = new TreeMap<>();
        Map<String, Medicine> linkedMap = new LinkedHashMap<>();

        List<String> insertionKeys = new ArrayList<>();

        System.out.println("\n========================================");
        System.out.println("NUMBER OF MEDICINES: " + size);
        System.out.println("========================================");

        // Measure insertion time
        long start = System.nanoTime();

        for (int i = size; i >= 1; i--) {
            Medicine m = createMedicine(i);
            hashMap.put(m.code, m);
            insertionKeys.add(m.code);
        }

        long hashPut = System.nanoTime() - start;

        start = System.nanoTime();

        for (int i = size; i >= 1; i--) {
            Medicine m = createMedicine(i);
            treeMap.put(m.code, m);
        }

        long treePut = System.nanoTime() - start;

        start = System.nanoTime();

        for (int i = size; i >= 1; i--) {
            Medicine m = createMedicine(i);
            linkedMap.put(m.code, m);
        }

        long linkedPut = System.nanoTime() - start;

        System.out.println("\nPUT OPERATION:");
        System.out.printf("HashMap       : %.3f ms%n",
                hashPut / 1_000_000.0);
        System.out.printf("TreeMap       : %.3f ms%n",
                treePut / 1_000_000.0);
        System.out.printf("LinkedHashMap : %.3f ms%n",
                linkedPut / 1_000_000.0);

        // Lookup experiment
        System.out.println("\n100,000 STOCK LOOKUPS:");

        System.out.printf("HashMap       : %.3f ms%n",
                measureLookup(hashMap, size));

        System.out.printf("TreeMap       : %.3f ms%n",
                measureLookup(treeMap, size));

        System.out.printf("LinkedHashMap : %.3f ms%n",
                measureLookup(linkedMap, size));

        // Iteration experiment
        System.out.println("\nITERATION TIME:");

        System.out.printf("HashMap       : %.3f ms%n",
                measureIteration(hashMap));

        System.out.printf("TreeMap       : %.3f ms%n",
                measureIteration(treeMap));

        System.out.printf("LinkedHashMap : %.3f ms%n",
                measureIteration(linkedMap));

        // Code-order reports
        System.out.println("\nCODE-ORDER REPORT TIME:");

        System.out.printf("HashMap + sorting       : %.3f ms%n",
                measureCodeReport(hashMap));

        System.out.printf("TreeMap                 : %.3f ms%n",
                measureIteration(treeMap));

        System.out.printf("LinkedHashMap + sorting : %.3f ms%n",
                measureCodeReport(linkedMap));

        // Insertion-order reports
        System.out.println("\nINSERTION-ORDER REPORT TIME:");

        System.out.printf("HashMap + saved keys       : %.3f ms%n",
                measureInsertionReport(hashMap, insertionKeys));

        System.out.printf("TreeMap + saved keys       : %.3f ms%n",
                measureInsertionReport(treeMap, insertionKeys));

        System.out.printf("LinkedHashMap              : %.3f ms%n",
                measureIteration(linkedMap));

        // Verify report order
        List<Medicine> hashCodeReport =
                codeOrderReport(hashMap);

        List<Medicine> treeCodeReport =
                new ArrayList<>(treeMap.values());

        List<Medicine> linkedCodeReport =
                codeOrderReport(linkedMap);

        List<Medicine> hashInsertionReport =
                insertionOrderReport(hashMap, insertionKeys);

        List<Medicine> linkedInsertionReport =
                new ArrayList<>(linkedMap.values());

        System.out.println("\nORDER VERIFICATION:");

        System.out.println("HashMap code order: "
                + isCodeSorted(hashCodeReport));

        System.out.println("TreeMap code order: "
                + isCodeSorted(treeCodeReport));

        System.out.println("LinkedHashMap code order without sorting: "
                + isCodeSorted(linkedInsertionReport));

        System.out.println("HashMap insertion order with saved keys: "
                + isInsertionOrder(hashInsertionReport, insertionKeys));

        System.out.println("LinkedHashMap insertion order: "
                + isInsertionOrder(linkedInsertionReport, insertionKeys));

        // Display sample reports
        System.out.println("\nSAMPLE CODE-ORDER REPORT:");

        System.out.println(treeCodeReport.get(0));
        System.out.println(treeCodeReport.get(size - 1));

        System.out.println("\nSAMPLE INSERTION-ORDER REPORT:");

        System.out.println(linkedInsertionReport.get(0));
        System.out.println(linkedInsertionReport.get(size - 1));
    }

    public static void main(String[] args) {

        System.out.println("HOSPITAL PHARMACY INVENTORY");
        System.out.println("HASHMAP vs TREEMAP vs LINKEDHASHMAP");

        int[] sizes = {100, 1000, 10000};

        for (int size : sizes) {
            runExperiment(size);
        }

        System.out.println("\n========================================");
        System.out.println("COMPLEXITY ANALYSIS");
        System.out.println("========================================");

        System.out.println("Operation       HashMap    TreeMap    LinkedHashMap");
        System.out.println("Put             O(1)*      O(log n)   O(1)*");
        System.out.println("Get             O(1)*      O(log n)   O(1)*");
        System.out.println("Iteration       O(n)       O(n)       O(n)");
        System.out.println("Code sorting    O(n log n) O(n)       O(n log n)");
        System.out.println("Insertion order Extra work O(n)       O(n)");

        System.out.println("\nARRAY BINARY SEARCH:");
        System.out.println("Lookup          O(log n)");
        System.out.println("Insertion       O(n)");
        System.out.println("Code report     O(n)");
        System.out.println("Space           O(n)");

        System.out.println("\nFINAL RECOMMENDATION:");
        System.out.println("HashMap: Fast medicine stock lookup");
        System.out.println("TreeMap: Medicines automatically sorted by code");
        System.out.println("LinkedHashMap: Preserves insertion order");

        System.out.println("\nACCEPTABLE LOOKUP TIME: Below 1 ms per lookup");
        System.out.println("PROGRAM EXECUTED SUCCESSFULLY");
    }
}
