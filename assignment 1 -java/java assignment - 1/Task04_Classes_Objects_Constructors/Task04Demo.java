/** Task 4 - demonstrates constructors, object creation in memory and the cost of three creation styles. */
public class Task04Demo {

    /** Alternative design 1: default constructor + setters (object is invalid until all setters run). */
    static class MutableTicket {
        int ticketNo; Vehicle vehicle; int floor; int slot; int entryMinute;
        void setTicketNo(int t) { ticketNo = t; }
        void setVehicle(Vehicle v) { vehicle = v; }
        void setSlot(int f, int s) { floor = f; slot = s; }
        void setEntryMinute(int m) { entryMinute = m; }
    }

    /** Alternative design 2: static factory method with a descriptive name. */
    static class FactoryTicket {
        final int ticketNo; final Vehicle vehicle; final int floor; final int slot;
        private FactoryTicket(int t, Vehicle v, int f, int s) { ticketNo = t; vehicle = v; floor = f; slot = s; }
        static FactoryTicket forVehicle(int t, Vehicle v, int f, int s) {
            if (v == null || f < 0 || s < 0) throw new IllegalArgumentException("invalid ticket data");
            return new FactoryTicket(t, v, f, s);
        }
    }

    public static void main(String[] args) {
        // ----- constructors in action -----
        Vehicle v1 = new Vehicle();                       // default
        Vehicle v2 = new Vehicle("TN01AB1111");           // overloaded (type defaults to CAR)
        Vehicle v3 = new Vehicle("TN02CD2222", "EV");     // parameterised
        Vehicle v4 = new Vehicle(v3);                     // copy constructor
        System.out.println(v1 + " | " + v2 + " | " + v3 + " | " + v4);

        ParkingTicket t1 = new ParkingTicket(1001, v3, 0, 1, 540);
        ParkingTicket t2 = new ParkingTicket(1002, v2, 555);       // slot not yet allocated
        ParkingTicket t0 = new ParkingTicket();                    // placeholder
        ParkingTicket dup = new ParkingTicket(t1);                 // duplicate re-issue
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t0);
        System.out.println(dup);

        // ----- reference semantics (stack / heap) -----
        ParkingTicket alias = t1;                                  // copies the REFERENCE only
        System.out.println("t1 == alias     : " + (t1 == alias));           // true  (same heap object)
        System.out.println("t1 == dup       : " + (t1 == dup));             // false (different heap objects)
        System.out.println("same ticketNo   : " + (t1.getTicketNo() == dup.getTicketNo()));
        System.out.println("shared Vehicle? : " + (t1.getVehicle() == dup.getVehicle()) + " (copy constructor made a new Vehicle)");

        // ----- cost of creating n tickets in three ways -----
        int n = 2_000_000;
        Vehicle shared = new Vehicle("TN09ZZ9999", "CAR");
        long sink = 0;

        long t = System.nanoTime();
        for (int i = 0; i < n; i++) { sink += new ParkingTicket(i, shared, 0, i % 50, 600).getTicketNo(); }
        long ctorMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        for (int i = 0; i < n; i++) {
            MutableTicket m = new MutableTicket();
            m.setTicketNo(i); m.setVehicle(shared); m.setSlot(0, i % 50); m.setEntryMinute(600);
            sink += m.ticketNo;
        }
        long setterMs = (System.nanoTime() - t) / 1_000_000;

        t = System.nanoTime();
        for (int i = 0; i < n; i++) { sink += FactoryTicket.forVehicle(i, shared, 0, i % 50).ticketNo; }
        long factoryMs = (System.nanoTime() - t) / 1_000_000;

        System.out.println("\nCreating " + n + " tickets (ms): constructor=" + ctorMs
                + ", default+setters=" + setterMs + ", static factory=" + factoryMs + " (sink " + sink + ")");
    }
}
