import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {
    public void insert(String name, String regNo, int eventId) throws SQLException {
        String sql = "INSERT INTO registrations(student_name, register_number, event_id) VALUES(?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, regNo); ps.setInt(3, eventId);
            ps.executeUpdate();
        }
    }

    public List<Registration> findPage(Integer eventId, int limit, int offset) throws SQLException {
        String sql = "SELECT r.id, r.student_name, r.register_number, e.event_name "
                + "FROM registrations r JOIN events e ON r.event_id=e.id "
                + (eventId == null ? "" : "WHERE e.id=? ")
                + "ORDER BY r.id LIMIT ? OFFSET ?";
        List<Registration> rows = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int index = 1;
            if (eventId != null) ps.setInt(index++, eventId);
            ps.setInt(index++, limit); ps.setInt(index, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) rows.add(new Registration(rs.getInt("id"),
                        rs.getString("student_name"), rs.getString("register_number"),
                        rs.getString("event_name")));
            }
        }
        return rows;
    }

    public List<String[]> findAllForExport() throws SQLException {
        String sql = "SELECT r.student_name, r.register_number, e.event_name "
                + "FROM registrations r JOIN events e ON r.event_id=e.id ORDER BY r.id";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
        }
        return rows;
    }

    public List<String[]> getEvents() throws SQLException {
        List<String[]> events = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,event_name FROM events ORDER BY event_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) events.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
        }
        return events;
    }
}
