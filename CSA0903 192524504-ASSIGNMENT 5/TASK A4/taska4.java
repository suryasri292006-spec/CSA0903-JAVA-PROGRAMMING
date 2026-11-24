
import java.io.*;

// Student class
class Student implements Serializable {

    int id;
    String name;
    String department;

    Student(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    void display() {
        System.out.println("Student ID: " + id);
        System.out.println("Student Name: " + name);
        System.out.println("Department: " + department);
    }
}

public class Main {

    public static void main(String[] args) {

        try {
            // Creating student object
            Student s1 = new Student(101, "Arun", "CSE");

            // Serialization
            FileOutputStream fout =
                new FileOutputStream("student.txt");

            ObjectOutputStream out =
                new ObjectOutputStream(fout);

            out.writeObject(s1);
            out.close();
            fout.close();

            System.out.println("Serialization completed.");

            // Deserialization
            FileInputStream fin =
                new FileInputStream("student.txt");

            ObjectInputStream in =
                new ObjectInputStream(fin);

            Student s2 = (Student) in.readObject();

            in.close();
            fin.close();

            System.out.println("\nDeserialized Student Details:");
            s2.display();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
