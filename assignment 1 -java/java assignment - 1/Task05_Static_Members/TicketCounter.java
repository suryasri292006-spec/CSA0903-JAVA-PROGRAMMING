import java.lang.ref.WeakReference;

/** Task 5 - static members: ticket counter, occupancy, static block, object life cycle. */
public class TicketCounter {

    // ----- static (class-level) data shared by every ticket -----
    private static final String PREFIX;           // set once in the static block
    private static int nextNumber;                // never reused: only increases
    private static int activeTickets;             // total occupancy of the facility

    static {                                      // static block runs once, when the class is loaded
        PREFIX = "CP-";
        nextNumber = 1000;
        activeTickets = 0;
    }

    /** Static method: needs no TicketCounter object. */
    public static String issueNumber() {
        activeTickets++;
        return PREFIX + (nextNumber++);
    }

    public static void ticketClosed() { activeTickets--; }
    public static int getActiveTickets() { return activeTickets; }

    // ===================== demo helpers =====================

    /** A ticket whose number comes from the shared static counter. */
    static class Ticket {
        private final String number = issueNumber();   // instance field, filled from static state
        private final String regNo;
        private boolean closed;
        Ticket(String regNo) { this.regNo = regNo; }
        String getNumber() { return number; }
        boolean isClosed() { return closed; }
        void close() { if (!closed) { closed = true; ticketClosed(); } }
        @Override public String toString() { return number + "(" + regNo + (closed ? ",closed" : ",open") + ")"; }
    }

    /** WRONG design: the counter is an INSTANCE variable, so every facility restarts from 1000. */
    static class FacilityWithInstanceCounter {
        private int counter = 1000;
        String issue() { return "CP-" + (counter++); }
    }

    /** Object passed to a method: the REFERENCE is copied, the object is shared. */
    static void markClosed(Ticket t) { t.close(); }

    /** Object returned from a method: caller receives a reference to a new heap object. */
    static Ticket reissue(Ticket old) { return new Ticket(old.regNo); }

    public static void main(String[] args) {
        System.out.println("=== Static counter shared by all tickets ===");
        Ticket a = new Ticket("TN01AB1111");
        Ticket b = new Ticket("TN02CD2222");
        Ticket c = new Ticket("TN03EF3333");
        System.out.println(a + " " + b + " " + c);
        System.out.println("Active tickets = " + getActiveTickets());

        System.out.println("\n=== Object passing ===");
        markClosed(b);                                  // method changes the caller's object
        System.out.println(b + "  active=" + getActiveTickets());
        Ticket again = reissue(c);                      // method returns a new object
        System.out.println("Re-issued: " + again + "  active=" + getActiveTickets());

        System.out.println("\n=== Object life cycle / garbage collection ===");
        Ticket temp = new Ticket("TN04GH4444");
        WeakReference<Ticket> watcher = new WeakReference<>(temp);
        temp.close();                                   // vehicle left; object still reachable via 'temp'
        System.out.println("Closed but still referenced -> reachable: " + (watcher.get() != null));
        temp = null;                                    // last strong reference removed: eligible for GC
        System.gc();                                    // a request only; JVM decides
        System.out.println("After temp = null and System.gc() -> collected: " + (watcher.get() == null)
                + " (usually true; GC timing is not guaranteed)");

        System.out.println("\n=== Alternative: instance counter with two facilities ===");
        FacilityWithInstanceCounter north = new FacilityWithInstanceCounter();
        FacilityWithInstanceCounter south = new FacilityWithInstanceCounter();
        System.out.println("north issues " + north.issue() + ", south issues " + south.issue()
                + "  -> DUPLICATE ticket number across the campus");
        System.out.println("Static counter issues " + issueNumber() + " then " + issueNumber() + " -> always unique");
    }
}
