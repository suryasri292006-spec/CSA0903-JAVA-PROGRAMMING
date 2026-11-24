
import java.util.*;

public class Main {

    // Student class
    static class Student {
        int studentId;
        String studentName;
        String department;
        String email;

        Student(int id, String name, String dept, String email) {
            this.studentId = id;
            this.studentName = name;
            this.department = dept;
            this.email = email;
        }

        void display() {
            System.out.println("ID: " + studentId);
            System.out.println("Name: " + studentName);
            System.out.println("Department: " + department);
            System.out.println("Email: " + email);
            System.out.println("----------------------");
        }
    }

    // DAO interface
    interface Dao<T, K> {
        void insert(T obj);
        T findById(K id);
        List<T> findAll();
        void update(T obj);
        void delete(K id);
    }

    // Student DAO implementation
    static class StudentDao implements Dao<Student, Integer> {

        ArrayList<Student> students = new ArrayList<>();

        public void insert(Student s) {
            students.add(s);
            System.out.println("Student inserted successfully.");
        }

        public Student findById(Integer id) {
            for (Student s : students) {
                if (s.studentId == id) {
                    return s;
                }
            }
            return null;
        }

        public List<Student> findAll() {
            return students;
        }

        public void update(Student s) {
            Student old = findById(s.studentId);

            if (old != null) {
                old.studentName = s.studentName;
                old.department = s.department;
                old.email = s.email;
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("Student not found.");
            }
        }

        public void delete(Integer id) {
            Student s = findById(id);

            if (s != null) {
                students.remove(s);
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student not found.");
            }
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        StudentDao dao = new StudentDao();

        while (true) {
            System.out.println("\n--- STUDENT MANAGEMENT ---");
            System.out.println("1. Insert Student");
            System.out.println("2. Find Student");
            System.out.println("3. Display All Students");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Department: ");
                    String dept = sc.nextLine();

                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    dao.insert(new Student(id, name, dept, email));
                    break;

                case 2:
                    System.out.print("Enter Student ID: ");
                    int searchId = sc.nextInt();

                    Student result = dao.findById(searchId);

                    if (result != null) {
                        result.display();
                    } else {
                        System.out.println("Student not found.");
                    }
                    break;

                case 3:
                    for (Student s : dao.findAll()) {
                        s.display();
                    }
                    break;

                case 4:
                    System.out.print("Enter ID to update: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter New Name: ");
                    String newName = sc.nextLine();

                    System.out.print("Enter New Department: ");
                    String newDept = sc.nextLine();

                    System.out.print("Enter New Email: ");
                    String newEmail = sc.nextLine();

                    dao.update(new Student(updateId, newName,
                            newDept, newEmail));
                    break;

                case 5:
                    System.out.print("Enter ID to delete: ");
                    int deleteId = sc.nextInt();
                    dao.delete(deleteId);
                    break;

                case 6:
                    System.out.println("Program terminated.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
