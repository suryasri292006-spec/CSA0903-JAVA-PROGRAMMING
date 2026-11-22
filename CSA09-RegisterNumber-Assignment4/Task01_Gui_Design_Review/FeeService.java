import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Business/service layer: validates and records a fee payment. */
public class FeeService {
    private final Connection connection;

    public FeeService(Connection connection) {
        this.connection = connection;
    }

    public String pay(String registerNumber, String amountText) throws SQLException {
        if (registerNumber == null || registerNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required.");
        }
        final double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Enter a valid amount.");
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        String sql = "INSERT INTO fees (register_number, amount) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, registerNumber.trim());
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
        return "Payment recorded for " + registerNumber.trim()
                + " : Rs. " + String.format("%.2f", amount);
    }
}
