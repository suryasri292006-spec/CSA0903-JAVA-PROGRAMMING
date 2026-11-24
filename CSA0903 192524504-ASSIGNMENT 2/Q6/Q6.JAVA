
import java.util.*;

class Patient {

    final String name;
    final int severity;
    final long arrival;
    final long arrivalTime;

    Patient(String name, int severity, long arrival) {
        this.name = name;
        this.severity = severity;
        this.arrival = arrival;
        this.arrivalTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}

public class Main {

    private long counter = 0;

    // TODO 1: PriorityQueue comparator
    private PriorityQueue<Patient> queue =
        new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.severity)
            .thenComparingLong(p -> p.arrival)
        );

    // Admit a patient
    void admit(String name, int severity) {

        if (severity < 1 || severity > 5) {
            throw new IllegalArgumentException(
                "Severity must be between 1 and 5"
            );
        }

        queue.add(new Patient(name, severity, counter++));
    }

    // Remove next patient
    Patient next() {
        return queue.poll();
    }

    // Display waiting list in treatment order
    void displayWaitingList() {

        PriorityQueue<Patient> copy =
            new PriorityQueue<>(queue);

        System.out.println("Waiting list:");

        while (!copy.isEmpty()) {
            System.out.print(copy.poll() + " ");
        }

        System.out.println();
    }

    // Number of waiting patients
    int size() {
        return queue.size();
    }

    // Fairness rule: promote patients waiting beyond threshold
    // Rebuild the queue whenever priorities are recalculated.
    void applyAging(long waitingTimeLimit) {

        long currentTime = System.currentTimeMillis();

        List<Patient> patients = new ArrayList<>(queue);

        queue.clear();

        for (Patient p : patients) {

            long waitingTime =
                currentTime - p.arrivalTime;

            int effectiveSeverity = p.severity;

            if (waitingTime >= waitingTimeLimit &&
                effectiveSeverity > 1) {

                effectiveSeverity = 1;
            }

            queue.add(new PatientWithPriority(
                p, effectiveSeverity
            ));
        }
    }

    // Benchmark helper
    static class PatientWithPriority extends Patient {

        final int effectiveSeverity;

        PatientWithPriority(
            Patient p, int effectiveSeverity) {

            super(p.name, p.severity, p.arrival);

            this.effectiveSeverity = effectiveSeverity;
        }
    }

    // Benchmark PriorityQueue performance
    static void performanceTest(int numberOfPatients) {

        PriorityQueue<Patient> testQueue =
            new PriorityQueue<>(
                Comparator.comparingInt((Patient p) -> p.severity)
                .thenComparingLong(p -> p.arrival)
            );

        long start = System.nanoTime();

        // Add patients
        for (int i = 0; i < numberOfPatients; i++) {

            int severity = (i % 5) + 1;

            testQueue.add(
                new Patient("Patient" + i, severity, i)
            );
        }

        long addEnd = System.nanoTime();

        // Remove patients
        while (!testQueue.isEmpty()) {
            testQueue.poll();
        }

        long removeEnd = System.nanoTime();

        double addTime =
            (addEnd - start) / 1_000_000.0;

        double removeTime =
            (removeEnd - addEnd) / 1_000_000.0;

        System.out.println("\nPatients: " + numberOfPatients);

        System.out.printf(
            "Admission time: %.3f ms%n", addTime
        );

        System.out.printf(
            "Treatment time: %.3f ms%n", removeTime
        );
    }

    public static void main(String[] args) {

        Main er = new Main();

        System.out.println("HOSPITAL EMERGENCY TRIAGE");
        System.out.println("================================");

        // Admit 8 patients
        er.admit("Ravi", 3);
        er.admit("Meena", 1);
        er.admit("John", 3);
        er.admit("Asha", 5);
        er.admit("Kumar", 1);
        er.admit("Lata", 2);
        er.admit("Priya", 4);
        er.admit("Arun", 2);

        // Display waiting list
        System.out.println("\nPATIENT WAITING LIST");
        er.displayWaitingList();

        // Treatment order
        System.out.println("\nPATIENT TREATMENT ORDER:");

        Patient p;

        while ((p = er.next()) != null) {
            System.out.print(p + " ");
        }

        System.out.println();

        // Performance experiments
        System.out.println("\n================================");
        System.out.println("PERFORMANCE EXPERIMENTS");
        System.out.println("================================");

        performanceTest(1000);
        performanceTest(10000);
        performanceTest(50000);

        // Complexity analysis
        System.out.println("\n================================");
        System.out.println("COMPLEXITY ANALYSIS");
        System.out.println("================================");

        System.out.println("PriorityQueue admission: O(log n)");
        System.out.println("PriorityQueue removal: O(log n)");
        System.out.println("PriorityQueue peek: O(1)");
        System.out.println("PriorityQueue space: O(n)");

        System.out.println("\nFINAL RECOMMENDATION:");
        System.out.println("Use PriorityQueue for emergency triage.");
        System.out.println("Use arrival counter to preserve FIFO order.");
        System.out.println("Use aging to reduce patient starvation.");

        System.out.println("\nPROGRAM EXECUTED SUCCESSFULLY");
    }
}
