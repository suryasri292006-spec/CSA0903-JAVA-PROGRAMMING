package parking.model;

/** Who is parking. Decides whether a pass can exist (students/staff) or not (visitors). */
public enum UserType {
    STUDENT, STAFF, VISITOR;

    /** Converts text such as "student" to the enum; rejects anything else. */
    public static UserType parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("User type is missing");
        }
        try {
            return valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown user type: " + text);
        }
    }
}
