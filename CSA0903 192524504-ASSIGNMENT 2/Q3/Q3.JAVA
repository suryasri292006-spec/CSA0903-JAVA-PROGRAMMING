
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.HashMap;

public class Main {

    // Student class WITHOUT hashCode()
    static class BrokenStudent {

        String regNo;
        String name;

        BrokenStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o)
                return true;

            if (!(o instanceof BrokenStudent))
                return false;

            return regNo.equals(((BrokenStudent) o).regNo);
        }

        @Override
        public String toString() {
            return regNo + " " + name;
        }
    }

    // Corrected Student class
    static class Student {

        private final String regNo;
        private final String name;

        Student(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o)
                return true;

            if (!(o instanceof Student))
                return false;

            Student s = (Student) o;

            return regNo.equals(s.regNo);
        }

        // TODO 1: Correct hashCode()
        @Override
        public int hashCode() {
            return regNo.hashCode();
        }

        @Override
        public String toString() {
            return regNo + " " + name;
        }
    }

    // Mutable student for third experiment
    static class MutableStudent {

        String regNo;

        MutableStudent(String regNo) {
            this.regNo = regNo;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o)
                return true;

            if (!(o instanceof MutableStudent))
                return false;

            return regNo.equals(((MutableStudent) o).regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }
    }

    public static void main(String[] args) {

        System.out.println("COLLEGE FEST ATTENDANCE");
        System.out.println("================================");

        // TEST 1: Same object scanned twice
        System.out.println("\nTEST 1: Same object scanned twice");

        Set<BrokenStudent> test1 = new HashSet<>();

        BrokenStudent s1 =
                new BrokenStudent("192311001", "Arun");

        BrokenStudent s2 =
                new BrokenStudent("192311002", "Divya");

        test1.add(s1);
        test1.add(s2);
        test1.add(s1);

        System.out.println("Expected size: 2");
        System.out.println("Actual size: " + test1.size());

        // TEST 2: Different objects with same regNo
        System.out.println("\nTEST 2: Duplicate student scans");

        Set<BrokenStudent> test2 = new HashSet<>();

        test2.add(new BrokenStudent("192311001", "Arun"));
        test2.add(new BrokenStudent("192311002", "Divya"));
        test2.add(new BrokenStudent("192311001", "Arun"));

        System.out.println("Expected size: 2");
        System.out.println("Actual size: " + test2.size());

        // TEST 3: Changing field after insertion
        System.out.println("\nTEST 3: Changing regNo after insertion");

        Set<MutableStudent> test3 = new HashSet<>();

        MutableStudent m1 = new MutableStudent("A");

        test3.add(m1);

        System.out.println("Before modification:");
        System.out.println("Contains student: " + test3.contains(m1));

        m1.regNo = "Z";

        System.out.println("After modification:");
        System.out.println("Contains student: " + test3.contains(m1));

        test3.add(m1);

        System.out.println("Set size after adding again: " + test3.size());

        // TEST 4: Corrected Student class
        System.out.println("\nTEST 4: Corrected hashCode()");

        Set<Student> present = new HashSet<>();

        present.add(new Student("192311001", "Arun"));
        present.add(new Student("192311002", "Divya"));
        present.add(new Student("192311001", "Arun"));

        System.out.println("Expected size: 2");
        System.out.println("Actual size: " + present.size());

        System.out.println("Unique students:");

        for (Student s : present) {
            System.out.println(s);
        }

        // Alternative 1: TreeSet
        System.out.println("\nTEST 5: TreeSet solution");

        Set<Student> treeSet = new TreeSet<>(
            Comparator.comparing(s -> s.regNo)
        );

        treeSet.add(new Student("192311001", "Arun"));
        treeSet.add(new Student("192311002", "Divya"));
        treeSet.add(new Student("192311001", "Arun"));

        System.out.println("TreeSet size: " + treeSet.size());

        // Alternative 2: HashMap
        System.out.println("\nTEST 6: HashMap solution");

        HashMap<String, Student> map = new HashMap<>();

        Student a = new Student("192311001", "Arun");
        Student b = new Student("192311002", "Divya");
        Student c = new Student("192311001", "Arun");

        map.put(a.regNo, a);
        map.put(b.regNo, b);
        map.put(c.regNo, c);

        System.out.println("HashMap size: " + map.size());

        System.out.println("\n================================");
        System.out.println("PROGRAM EXECUTED SUCCESSFULLY");
    }
}
