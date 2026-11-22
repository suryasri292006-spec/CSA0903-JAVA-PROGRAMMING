import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class FrameListenerConverter extends JFrame implements ActionListener {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> from = new JComboBox<>(new String[]{"INR", "USD", "EUR"});
    private final JComboBox<String> to = new JComboBox<>(new String[]{"USD", "EUR", "INR"});
    private final JLabel result = new JLabel("Result appears here");
    public FrameListenerConverter() {
        super("Converter – Frame Listener");
        build();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); pack(); setLocationRelativeTo(null);
    }
    private void build() {
        setLayout(new FlowLayout());
        JButton convert = new JButton("Convert");
        add(new JLabel("Amount:")); add(amount); add(from); add(new JLabel("to")); add(to);
        add(convert); add(result);
        convert.addActionListener(this); amount.addActionListener(this);
    }
    @Override public void actionPerformed(ActionEvent e) { convert(); }
    private void convert() {
        try {
            double value = Double.parseDouble(amount.getText().trim());
            if (!Double.isFinite(value) || value < 0) throw new NumberFormatException();
            double inr = value * rate((String) from.getSelectedItem());
            double answer = inr / rate((String) to.getSelectedItem());
            String text = String.format("%.2f %s", answer, to.getSelectedItem());
            result.setText(text); JOptionPane.showMessageDialog(this, text);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount"); JOptionPane.showMessageDialog(this, "Invalid amount");
        }
    }
    private double rate(String c) { return switch(c) { case "INR" -> 1.0; case "USD" -> 83.0; default -> 90.0; }; }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new FrameListenerConverter().setVisible(true)); }
}
