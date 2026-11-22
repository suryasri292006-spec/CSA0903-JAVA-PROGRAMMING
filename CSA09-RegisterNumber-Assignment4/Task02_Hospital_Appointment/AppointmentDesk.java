import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AppointmentDesk extends JFrame {
    private final JTextField patientId = new JTextField(14);
    private final JComboBox<String> department = new JComboBox<>(
            new String[]{"Cardiology", "Orthopedics", "Dermatology"});
    private final JComboBox<Doctor> doctor = new JComboBox<>();
    private final JComboBox<String> slot = new JComboBox<>(
            new String[]{"09:00", "10:00", "11:00", "14:00", "15:00"});
    private final JButton book = new JButton("Book Appointment");
    private final AppointmentService service = new AppointmentService();
    private boolean bookingInProgress = false;

    public AppointmentDesk() {
        super("Hospital Outpatient Appointment Desk");
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Patient ID")); form.add(patientId);
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Doctor")); form.add(doctor);
        form.add(new JLabel("Time slot")); form.add(slot);
        form.add(new JLabel("Action")); form.add(book);
        add(form);
        department.addActionListener(e -> refreshDoctors());
        book.addActionListener(e -> makeBooking());
        refreshDoctors();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    int answer = JOptionPane.showConfirmDialog(AppointmentDesk.this,
                            "A booking is in progress. Close anyway?", "Confirm close",
                            JOptionPane.YES_NO_OPTION);
                    if (answer != JOptionPane.YES_OPTION) return;
                }
                dispose();
            }
        });
        setSize(460, 260);
        setLocationRelativeTo(null);
    }

    private void refreshDoctors() {
        doctor.removeAllItems();
        for (Doctor d : service.doctorsFor((String) department.getSelectedItem())) doctor.addItem(d);
    }

    private void makeBooking() {
        bookingInProgress = true;
        book.setEnabled(false);
        try {
            Doctor selected = (Doctor) doctor.getSelectedItem();
            service.book(patientId.getText(), (String) department.getSelectedItem(),
                    selected == null ? "" : selected.getName(), (String) slot.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Appointment booked successfully.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking error", JOptionPane.WARNING_MESSAGE);
        } finally {
            bookingInProgress = false;
            book.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentDesk().setVisible(true));
    }
}
