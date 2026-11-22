import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

/** GUI layer: collects input and displays service results. */
public class FeeWindow extends JFrame {
    private final JTextField regNo = new JTextField(14);
    private final JTextField amount = new JTextField(14);
    private final JLabel status = new JLabel("Enter payment details.");
    private final JTextArea receiptArea = new JTextArea(5, 28);
    private FeeService service;

    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Register number:")); form.add(regNo);
        form.add(new JLabel("Amount (Rs.):")); form.add(amount);
        JButton pay = new JButton("Pay");
        JButton clear = new JButton("Clear");
        JButton receipt = new JButton("Receipt");
        JPanel actions = new JPanel(new FlowLayout());
        actions.add(pay); actions.add(clear); actions.add(receipt);
        receiptArea.setEditable(false);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        add(status, BorderLayout.PAGE_END);

        pay.addActionListener(e -> performPayment());
        clear.addActionListener(e -> {
            regNo.setText(""); amount.setText("");
            receiptArea.setText(""); status.setText("Fields cleared.");
        });
        receipt.addActionListener(e -> {
            if (receiptArea.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "No receipt is available yet.");
            } else {
                JOptionPane.showMessageDialog(this, receiptArea.getText(), "Receipt",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Database configuration is supplied through environment variables.
        try {
            String url = System.getenv().getOrDefault("FEE_DB_URL",
                    "jdbc:mysql://localhost:3306/campus_events");
            String user = System.getenv().getOrDefault("FEE_DB_USER", "root");
            String password = System.getenv().getOrDefault("FEE_DB_PASSWORD", "");
            Connection c = DriverManager.getConnection(url, user, password);
            service = new FeeService(c);
        } catch (SQLException ex) {
            status.setText("Database unavailable. Configure FEE_DB_URL/USER/PASSWORD.");
            pay.setEnabled(false);
        }

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(430, 280);
        setLocationRelativeTo(null);
    }

    private void performPayment() {
        if (service == null) return;
        try {
            String message = service.pay(regNo.getText(), amount.getText());
            receiptArea.setText(message);
            status.setText("Payment recorded.");
            JOptionPane.showMessageDialog(this, message);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Payment could not be recorded: "
                    + ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeeWindow().setVisible(true));
    }
}
