import java.util.*;

public class AppointmentService {
    private final Map<String, List<Doctor>> doctorsByDepartment = new LinkedHashMap<>();
    private final Set<String> bookedSlots = new HashSet<>();

    public AppointmentService() {
        doctorsByDepartment.put("Cardiology", List.of(new Doctor("Dr. Arun"), new Doctor("Dr. Meena")));
        doctorsByDepartment.put("Orthopedics", List.of(new Doctor("Dr. Ravi"), new Doctor("Dr. Nila")));
        doctorsByDepartment.put("Dermatology", List.of(new Doctor("Dr. Kavi"), new Doctor("Dr. Sara")));
    }
    public List<Doctor> doctorsFor(String department) {
        return doctorsByDepartment.getOrDefault(department, List.of());
    }
    public void book(String patientId, String department, String doctor, String slot) {
        if (patientId == null || patientId.isBlank()) throw new IllegalArgumentException("Patient ID is required.");
        if (department == null || department.isBlank() || doctor == null || doctor.isBlank())
            throw new IllegalArgumentException("Select a department and doctor.");
        if (slot == null || slot.isBlank()) throw new IllegalArgumentException("Select a time slot.");
        String key = doctor + "|" + slot;
        if (!bookedSlots.add(key)) throw new IllegalArgumentException("That doctor and time slot are already booked.");
    }
}
