
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/hosteldb";
        String user = "root";
        String password = "your_password";

        try {
            Connection con =
                DriverManager.getConnection(url, user, password);

            // Transaction handling
            con.setAutoCommit(false);

            try {
                // Deduct Rs.500 from hostel wallet
                PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE wallet SET balance = balance - 500 WHERE id = 1");

                ps1.executeUpdate();

                // Add Rs.500 to canteen wallet
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE wallet SET balance = balance + 500 WHERE id = 2");

                ps2.executeUpdate();

                // Save both updates
                con.commit();

                System.out.println("Transfer Successful.");

            } catch (SQLException e) {

                // Cancel both updates if an error occurs
                con.rollback();

                System.out.println("Transfer Failed.");
            }

            // Secure login using PreparedStatement
            String sql =
                "SELECT * FROM users WHERE id = ? AND pwd = ?";

            PreparedStatement ps3 = con.prepareStatement(sql);

            ps3.setString(1, "101");
            ps3.setString(2, "student123");

            ResultSet rs = ps3.executeQuery();

            if (rs.next()) {
                System.out.println("Login Successful.");
            } else {
                System.out.println("Invalid Login.");
            }

            con.setAutoCommit(true);
            con.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}
