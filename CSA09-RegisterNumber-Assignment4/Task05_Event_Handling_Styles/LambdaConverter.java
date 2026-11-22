import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class LambdaConverter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> from = new JComboBox<>(new String[]{"INR", "USD", "EUR"});
    private final JComboBox<String> to = new JComboBox<>(new String[]{"USD", "EUR", "INR"});
    private final JLabel result = new JLabel("Result appears here");
    public LambdaConverter() {
        super("Converter – Lambda");
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("Amount:")); add(amount); add(from); add(new JLabel("to")); add(to); add(convert); add(result);
        ActionListener handler = e -> convert();
        convert.addActionListener(handler);
        amount.addActionListener(handler);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { dispose(); }});
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); pack(); setLocationRelativeTo(null);
    }
    private void convert() {
        try {
            double v = Double.parseDouble(amount.getText().trim());
            if (!Double.isFinite(v) || v < 0) throw new NumberFormatException();
            double answer = v * rate((String)from.getSelectedItem()) / rate((String)to.getSelectedItem());
            String s = String.format("%.2f %s", answer, to.getSelectedItem());
            result.setText(s); JOptionPane.showMessageDialog(this, s);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount"); JOptionPane.showMessageDialog(this, "Invalid amount");
        }
    }
    private double rate(String c) { return switch(c) { case "INR" -> 1.0; case "USD" -> 83.0; default -> 90.0; }; }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new LambdaConverter().setVisible(true)); }
}
