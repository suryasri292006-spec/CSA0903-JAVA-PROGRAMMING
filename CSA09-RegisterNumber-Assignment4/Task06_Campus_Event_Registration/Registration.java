public class Registration {
    private final int id;
    private final String studentName, registerNumber, eventName;
    public Registration(int id, String studentName, String registerNumber, String eventName) {
        this.id = id; this.studentName = studentName; this.registerNumber = registerNumber; this.eventName = eventName;
    }
    public int getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getRegisterNumber() { return registerNumber; }
    public String getEventName() { return eventName; }
}
