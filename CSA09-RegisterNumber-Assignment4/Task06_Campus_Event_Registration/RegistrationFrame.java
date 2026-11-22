import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RegistrationFrame extends JFrame {
    private final RegistrationService service = new RegistrationService(new RegistrationDAO());
    private final JTextField studentName = new JTextField(14);
    private final JTextField registerNumber = new JTextField(14);
    private final JComboBox<EventItem> eventBox = new JComboBox<>();
    private final JComboBox<EventItem> filterBox = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Student Name", "Register Number", "Event"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Ready");
    private final JButton registerButton = new JButton("Register");
    private int page = 0;
    private static final int PAGE_SIZE = 25;

    private static class EventItem {
        final int id; final String name;
        EventItem(int id, String name) { this.id=id; this.name=name; }
        @Override public String toString() { return name; }
    }

    public RegistrationFrame() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8,8));
        buildMenu();
        JPanel form = new JPanel(new GridLayout(3,2,6,6));
        form.setBorder(BorderFactory.createTitledBorder("Student Registration"));
        form.add(new JLabel("Student name")); form.add(studentName);
        form.add(new JLabel("Register number")); form.add(registerNumber);
        form.add(new JLabel("Event")); form.add(eventBox);
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.add(form, BorderLayout.CENTER); formWrap.add(registerButton, BorderLayout.SOUTH);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.add(new JLabel("Filter event:")); filter.add(filterBox);
        JButton applyFilter = new JButton("Apply Filter");
        JButton prev = new JButton("Previous"), next = new JButton("Next"), export = new JButton("Export CSV");
        filter.add(applyFilter); filter.add(prev); filter.add(next); filter.add(export);
        JPanel north = new JPanel(new BorderLayout());
        north.add(formWrap, BorderLayout.NORTH); north.add(filter, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> registerAsync());
        applyFilter.addActionListener(e -> { page=0; loadPageAsync(); });
        prev.addActionListener(e -> { if(page>0){page--;loadPageAsync();} });
        next.addActionListener(e -> { page++; loadPageAsync(); });
        export.addActionListener(e -> exportAsync());
        loadEventsAsync();
        loadPageAsync();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(RegistrationFrame.this, "Close application?",
                        "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) dispose();
            }
        });
        setSize(900, 600); setLocationRelativeTo(null);
    }

    private void buildMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem refresh = new JMenuItem("Refresh");
        JMenuItem exit = new JMenuItem("Exit");
        refresh.addActionListener(e -> { page=0; loadPageAsync(); });
        exit.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        file.add(refresh); file.addSeparator(); file.add(exit);
        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Campus Event Registration – Java Swing and JDBC"));
        help.add(about); bar.add(file); bar.add(help); setJMenuBar(bar);
    }

    private Integer selectedFilterId() {
        EventItem item = (EventItem) filterBox.getSelectedItem();
        return item == null || item.id == 0 ? null : item.id;
    }

    private void loadEventsAsync() {
        new SwingWorker<List<String[]>,Void>() {
            protected List<String[]> doInBackground() throws Exception { return service.events(); }
            protected void done() {
                try {
                    eventBox.removeAllItems(); filterBox.removeAllItems();
                    filterBox.addItem(new EventItem(0,"All events"));
                    for(String[] e:get()) {
                        EventItem item = new EventItem(Integer.parseInt(e[0]),e[1]);
                        eventBox.addItem(item); filterBox.addItem(item);
                    }
                } catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private void registerAsync() {
        EventItem selected = (EventItem) eventBox.getSelectedItem();
        if (selected == null) { status.setText("No event available. Check database setup."); return; }
        registerButton.setEnabled(false);
        String name = studentName.getText(), reg = registerNumber.getText();
        int eventId = selected.id;
        status.setText("Saving registration...");
        new SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception { service.register(name,reg,eventId); return null; }
            protected void done() {
                registerButton.setEnabled(true);
                try {
                    get(); status.setText("Registration saved successfully.");
                    JOptionPane.showMessageDialog(RegistrationFrame.this,"Registration successful.");
                    studentName.setText(""); registerNumber.setText(""); page=0; loadPageAsync();
                } catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private void loadPageAsync() {
        status.setText("Loading registrations...");
        Integer eventId = selectedFilterId();
        int offset = page * PAGE_SIZE;
        new SwingWorker<List<Registration>,Void>() {
            protected List<Registration> doInBackground() throws Exception {
                return service.list(eventId,PAGE_SIZE,offset);
            }
            protected void done() {
                try {
                    List<Registration> rows=get(); model.setRowCount(0);
                    for(Registration r:rows) model.addRow(new Object[]{r.getId(),r.getStudentName(),r.getRegisterNumber(),r.getEventName()});
                    status.setText("Page " + (page+1) + " – " + rows.size() + " record(s).");
                    if(rows.isEmpty() && page>0) { page--; status.setText("No more records."); }
                } catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private void exportAsync() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("registrations.csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        java.io.File file=chooser.getSelectedFile();
        status.setText("Exporting...");
        new SwingWorker<List<String[]>,Void>() {
            protected List<String[]> doInBackground() throws Exception { return service.exportAll(); }
            protected void done() {
                try(BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    w.write("Student Name,Register Number,Event"); w.newLine();
                    for(String[] row:get()) {
                        w.write(csv(row[0])+","+csv(row[1])+","+csv(row[2])); w.newLine();
                    }
                    status.setText("CSV exported: " + file.getAbsolutePath());
                } catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private String csv(String value) { return "\"" + value.replace("\"","\"\"") + "\""; }
    private void showError(Exception ex) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        String message = cause.getMessage() == null ? cause.toString() : cause.getMessage();
        if (message.contains("Duplicate entry") || message.contains("uk_student_event"))
            message = "This student is already registered for this event.";
        status.setText("Operation failed.");
        JOptionPane.showMessageDialog(this,message,"Error",JOptionPane.ERROR_MESSAGE);
    }
}
