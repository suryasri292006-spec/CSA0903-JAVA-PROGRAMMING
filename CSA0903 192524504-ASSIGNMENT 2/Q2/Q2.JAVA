
import java.util.*;

class Song {
    int id;
    String name;

    Song(int id) {
        this.id = id;
        this.name = "Song" + id;
    }
}

public class Main {

    static final int RUNS = 5;
    static final int OPERATIONS = 2000;

    // Create ArrayList
    static ArrayList<Song> createArrayList(int size) {
        ArrayList<Song> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            list.add(new Song(i));
        }

        return list;
    }

    // Create LinkedList
    static LinkedList<Song> createLinkedList(int size) {
        LinkedList<Song> list = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            list.add(new Song(i));
        }

        return list;
    }

    // Create ArrayDeque
    static ArrayDeque<Song> createArrayDeque(int size) {
        ArrayDeque<Song> deque = new ArrayDeque<>();

        for (int i = 0; i < size; i++) {
            deque.addLast(new Song(i));
        }

        return deque;
    }

    // Measure add at end
    static long arrayListAdd(int size) {
        ArrayList<Song> list = createArrayList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.add(new Song(i));
        }

        return System.nanoTime() - start;
    }

    static long linkedListAdd(int size) {
        LinkedList<Song> list = createLinkedList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.add(new Song(i));
        }

        return System.nanoTime() - start;
    }

    static long dequeAdd(int size) {
        ArrayDeque<Song> deque = createArrayDeque(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            deque.addLast(new Song(i));
        }

        return System.nanoTime() - start;
    }

    // Insert and remove at front
    static long arrayListFront(int size) {
        ArrayList<Song> list = createArrayList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.add(0, new Song(i));
            list.remove(0);
        }

        return System.nanoTime() - start;
    }

    static long linkedListFront(int size) {
        LinkedList<Song> list = createLinkedList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.addFirst(new Song(i));
            list.removeFirst();
        }

        return System.nanoTime() - start;
    }

    static long dequeFront(int size) {
        ArrayDeque<Song> deque = createArrayDeque(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            deque.addFirst(new Song(i));
            deque.removeFirst();
        }

        return System.nanoTime() - start;
    }

    // Insert and remove in middle
    static long arrayListMiddle(int size) {
        ArrayList<Song> list = createArrayList(size);
        int middle = size / 2;

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.add(middle, new Song(i));
            list.remove(middle);
        }

        return System.nanoTime() - start;
    }

    static long linkedListMiddle(int size) {
        LinkedList<Song> list = createLinkedList(size);
        int middle = size / 2;

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            list.add(middle, new Song(i));
            list.remove(middle);
        }

        return System.nanoTime() - start;
    }

    // Access k-th element
    static long arrayListGet(int size) {
        ArrayList<Song> list = createArrayList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            Song s = list.get(size / 2);
        }

        return System.nanoTime() - start;
    }

    static long linkedListGet(int size) {
        LinkedList<Song> list = createLinkedList(size);

        long start = System.nanoTime();

        for (int i = 0; i < OPERATIONS; i++) {
            Song s = list.get(size / 2);
        }

        return System.nanoTime() - start;
    }

    // Iterate through complete list
    static long arrayListIterate(int size) {
        ArrayList<Song> list = createArrayList(size);

        long start = System.nanoTime();

        for (int r = 0; r < RUNS; r++) {
            for (Song s : list) {
                int id = s.id;
            }
        }

        return System.nanoTime() - start;
    }

    static long linkedListIterate(int size) {
        LinkedList<Song> list = createLinkedList(size);

        long start = System.nanoTime();

        for (int r = 0; r < RUNS; r++) {
            for (Song s : list) {
                int id = s.id;
            }
        }

        return System.nanoTime() - start;
    }

    static long dequeIterate(int size) {
        ArrayDeque<Song> deque = createArrayDeque(size);

        long start = System.nanoTime();

        for (int r = 0; r < RUNS; r++) {
            for (Song s : deque) {
                int id = s.id;
            }
        }

        return System.nanoTime() - start;
    }

    // Average execution time
    interface Test {
        long execute(int size);
    }

    static double average(Test test, int size) {
        long total = 0;

        for (int i = 0; i < RUNS; i++) {
            total += test.execute(size);
        }

        return total / (double) RUNS / 1_000_000;
    }

    public static void main(String[] args) {

        int[] sizes = {1000, 5000, 10000};

        System.out.println("MUSIC PLAYLIST PERFORMANCE COMPARISON");
        System.out.println("==============================================");

        for (int size : sizes) {

            System.out.println("\nPlaylist Size: " + size);
            System.out.println("----------------------------------------------");

            System.out.printf("%-22s %-12s %-12s %-12s%n",
                    "Operation", "ArrayList", "LinkedList", "ArrayDeque");

            System.out.printf("%-22s %-12.3f %-12.3f %-12.3f%n",
                    "Add at end (ms)",
                    average(Main::arrayListAdd, size),
                    average(Main::linkedListAdd, size),
                    average(Main::dequeAdd, size));

            System.out.printf("%-22s %-12.3f %-12.3f %-12.3f%n",
                    "Front insert/remove",
                    average(Main::arrayListFront, size),
                    average(Main::linkedListFront, size),
                    average(Main::dequeFront, size));

            System.out.printf("%-22s %-12.3f %-12.3f %-12s%n",
                    "Middle insert/remove",
                    average(Main::arrayListMiddle, size),
                    average(Main::linkedListMiddle, size),
                    "N/A");

            System.out.printf("%-22s %-12.3f %-12.3f %-12s%n",
                    "Get k-th element",
                    average(Main::arrayListGet, size),
                    average(Main::linkedListGet, size),
                    "N/A");

            System.out.printf("%-22s %-12.3f %-12.3f %-12.3f%n",
                    "Full iteration",
                    average(Main::arrayListIterate, size),
                    average(Main::linkedListIterate, size),
                    average(Main::dequeIterate, size));
        }

        System.out.println("\n==============================================");
        System.out.println("TIME COMPLEXITY COMPARISON");
        System.out.println("----------------------------------------------");
        System.out.println("Operation           ArrayList  LinkedList  ArrayDeque");
        System.out.println("Add at end          O(1)*      O(1)        O(1)*");
        System.out.println("Insert at front     O(n)       O(1)        O(1)");
        System.out.println("Insert in middle    O(n)       O(n)        Not supported");
        System.out.println("Get k-th element    O(1)       O(n)        Not supported");
        System.out.println("Iteration           O(n)       O(n)        O(n)");

        System.out.println("\nSPACE COMPLEXITY");
        System.out.println("ArrayList: Array slots, O(n)");
        System.out.println("LinkedList: Nodes with links, O(n)");
        System.out.println("ArrayDeque: Circular array slots, O(n)");

        System.out.println("\nFINAL RECOMMENDATION");
        System.out.println("Playlist: ArrayList");
        System.out.println("Up Next Queue: ArrayDeque");
        System.out.println("Program executed successfully!");
    }
}
