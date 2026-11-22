package parking.service;

/** Floors and slots. A jagged array allows a different number of slots on every floor. */
public class ParkingFacility {
    private final String[][] slots;        // slots[floor][slot] = vehicle number, or null when free

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) {
            throw new IllegalArgumentException("At least one floor is required");
        }
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] <= 0) {
                throw new IllegalArgumentException("Floor " + f + " must have at least one slot");
            }
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    /** Places the vehicle in the first free slot; returns {floor, slot} or null when full. */
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) {
            throw new IllegalArgumentException("Vehicle number is required");
        }
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] != null) {
                    continue;
                }
                slots[f][s] = vehicleNo;
                return new int[]{f, s};
            }
        }
        return null;
    }

    /** Frees a slot; false when the position is invalid or already free. */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        return true;
    }

    public int getFloorCount() { return slots.length; }
    public int getCapacity(int floor) { return slots[floor].length; }

    public int freeOnFloor(int floor) {
        int free = 0;
        for (String s : slots[floor]) {
            if (s == null) free++;
        }
        return free;
    }

    public int totalFree() {
        int free = 0;
        for (int f = 0; f < slots.length; f++) free += freeOnFloor(f);
        return free;
    }

    public int totalCapacity() {
        int total = 0;
        for (String[] floor : slots) total += floor.length;
        return total;
    }
}
