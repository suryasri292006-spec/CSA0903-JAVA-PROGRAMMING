import java.sql.SQLException;
import java.util.List;

public class RegistrationService {
    private final RegistrationDAO dao;
    public RegistrationService(RegistrationDAO dao) { this.dao = dao; }

    public void register(String name, String regNo, int eventId) throws SQLException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Student name is required.");
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("Register number is required.");
        if (!regNo.trim().matches("[A-Za-z0-9-]{4,25}"))
            throw new IllegalArgumentException("Register number must be 4–25 letters, digits or hyphens.");
        if (eventId <= 0) throw new IllegalArgumentException("Select an event.");
        dao.insert(name.trim(), regNo.trim(), eventId);
    }
    public List<Registration> list(Integer eventId, int limit, int offset) throws SQLException {
        if (limit < 1 || offset < 0) throw new IllegalArgumentException("Invalid page request.");
        return dao.findPage(eventId, limit, offset);
    }
    public List<String[]> exportAll() throws SQLException { return dao.findAllForExport(); }
    public List<String[]> events() throws SQLException { return dao.getEvents(); }
}
