
class ScannerStation extends Thread {

    int scripts;
    int scanned = 0;

    ScannerStation(String name, int scripts) {
        super(name);
        this.scripts = scripts;
    }

    public void run() {
        try {
            System.out.println(getName() + " started");

            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(500);
                scanned++;
            }

            System.out.println(getName() +
                    " completed. Scripts scanned: " + scanned);

        } catch (InterruptedException e) {
            System.out.println(getName() + " interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

class RunnableStation implements Runnable {

    int scripts;
    int scanned = 0;
    String name;

    RunnableStation(String name, int scripts) {
        this.name = name;
        this.scripts = scripts;
    }

    public void run() {
        try {
            System.out.println(name + " started");

            for (int i = 1; i <= scripts; i++) {
                Thread.sleep(500);
                scanned++;
            }

            System.out.println(name +
                    " completed. Scripts scanned: " + scanned);

        } catch (InterruptedException e) {
            System.out.println(name + " interrupted");
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {

    public static void main(String[] args) {

        int total = 0;

        System.out.println("=== METHOD 1: EXTENDING THREAD ===");

        ScannerStation s1 = new ScannerStation("Station 1", 5);
        ScannerStation s2 = new ScannerStation("Station 2", 6);
        ScannerStation s3 = new ScannerStation("Station 3", 7);
        ScannerStation s4 = new ScannerStation("Station 4", 8);

        s1.start();
        s2.start();
        s3.start();
        s4.start();

        System.out.println("Station 1 alive: " + s1.isAlive());

        try {
            s1.join();
            s2.join();
            s3.join();
            s4.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
            Thread.currentThread().interrupt();
            return;
        }

        total = s1.scanned + s2.scanned +
                s3.scanned + s4.scanned;

        System.out.println("All Thread stations completed.");
        System.out.println("Total scripts scanned: " + total);

        System.out.println("\n=== METHOD 2: IMPLEMENTING RUNNABLE ===");

        RunnableStation r1 = new RunnableStation("Station 1", 5);
        RunnableStation r2 = new RunnableStation("Station 2", 6);
        RunnableStation r3 = new RunnableStation("Station 3", 7);
        RunnableStation r4 = new RunnableStation("Station 4", 8);

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        Thread t4 = new Thread(r4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        System.out.println("Station 1 alive: " + t1.isAlive());

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
            Thread.currentThread().interrupt();
            return;
        }

        total = r1.scanned + r2.scanned +
                r3.scanned + r4.scanned;

        System.out.println("All Runnable stations completed.");
        System.out.println("Total scripts scanned: " + total);

        System.out.println("\nMain thread completed.");
    }
}
