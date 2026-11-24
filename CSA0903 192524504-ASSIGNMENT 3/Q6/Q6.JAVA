
class TokenCounter {

    private final int[] buffer = new int[5];

    private int count = 0;
    private int in = 0;
    private int out = 0;

    // TODO 1: Producer method
    public synchronized void put(int token)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;

        notifyAll();
    }

    // TODO 2: Consumer method
    public synchronized int take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        notifyAll();

        return token;
    }
}

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        TokenCounter counter = new TokenCounter();

        int[] collected = new int[2];

        // Producer thread (Cook)
        Thread cook = new Thread(() -> {

            try {
                for (int t = 1; t <= 20; t++) {
                    counter.put(t);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer threads (Students)
        Thread[] students = new Thread[2];

        for (int s = 0; s < 2; s++) {

            final int id = s;

            students[s] = new Thread(() -> {

                try {
                    for (int k = 0; k < 10; k++) {
                        collected[id] += counter.take();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Start producer and consumers
        cook.start();

        for (Thread t : students) {
            t.start();
        }

        // Wait for all threads to finish
        cook.join();

        for (Thread t : students) {
            t.join();
        }

        System.out.println(
            "Sum of tokens collected: "
            + (collected[0] + collected[1])
            + " (expected 210)");
    }
}
