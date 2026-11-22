import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibrarySearch extends JFrame {
    private final JTextField query = new JTextField(22);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"All", "Programming", "Mathematics", "Science"});
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Title", "Category", "Author"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Enter a title or select a category.");
    private final java.util.List<String[]> allBooks = new ArrayList<>();
    private java.util.List<String[]> matches = new ArrayList<>();
    private int page = 0;
    private static final int PAGE_SIZE = 5;

    public LibrarySearch() {
        super("Campus Library Search");
        allBooks.add(new String[]{"Java: A Beginner's Guide", "Programming", "Herbert Schildt"});
        allBooks.add(new String[]{"Effective Java", "Programming", "Joshua Bloch"});
        allBooks.add(new String[]{"Discrete Mathematics", "Mathematics", "Kenneth Rosen"});
        allBooks.add(new String[]{"Linear Algebra", "Mathematics", "Gilbert Strang"});
        allBooks.add(new String[]{"Physics Fundamentals", "Science", "Halliday"});
        allBooks.add(new String[]{"Database Systems", "Programming", "Elmasri"});
        allBooks.add(new String[]{"Chemistry Essentials", "Science", "Brown"});
        allBooks.add(new String[]{"Algorithms", "Programming", "Sedgewick"});

        setLayout(new BorderLayout(8, 8));
        JLabel title = new JLabel("Campus Library – Search Catalogue", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Title:")); searchRow.add(query);
        searchRow.add(category);
        JButton search = new JButton("Search"); searchRow.add(search);
        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(searchRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton prev = new JButton("Prev"), next = new JButton("Next"), reserve = new JButton("Reserve");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(prev); buttons.add(next); buttons.add(reserve);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.CENTER); bottom.add(status, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        search.addActionListener(e -> { page = 0; filter(); });
        query.addActionListener(e -> { page = 0; filter(); });
        prev.addActionListener(e -> { if (page > 0) { page--; showPage(); } });
        next.addActionListener(e -> { if ((page + 1) * PAGE_SIZE < matches.size()) { page++; showPage(); } });
        reserve.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) status.setText("Select a book before reserving.");
            else status.setText("Reservation request recorded for: " + model.getValueAt(row, 0));
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 440);
        setLocationRelativeTo(null);
        filter();
    }

    private void filter() {
        String text = query.getText().trim().toLowerCase();
        String cat = (String) category.getSelectedItem();
        matches = new ArrayList<>();
        for (String[] b : allBooks) {
            boolean titleMatch = b[0].toLowerCase().contains(text);
            boolean categoryMatch = "All".equals(cat) || b[1].equals(cat);
            if (titleMatch && categoryMatch) matches.add(b);
        }
        page = 0;
        showPage();
    }

    private void showPage() {
        model.setRowCount(0);
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, matches.size());
        for (int i = start; i < end; i++) model.addRow(matches.get(i));
        status.setText("Showing " + (matches.isEmpty() ? 0 : start + 1) + "–" + end
                + " of " + matches.size() + " matching books.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearch().setVisible(true));
    }
}
