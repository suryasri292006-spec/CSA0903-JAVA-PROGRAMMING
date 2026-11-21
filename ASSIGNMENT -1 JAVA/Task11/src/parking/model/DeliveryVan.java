package parking.model;

import java.time.LocalTime;

/** Task 12: car rate, doubled for hours that START between 9 AM and 5 PM. */
public class DeliveryVan extends Vehicle {
    public DeliveryVan(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return Car.RATE; }

    @Override public double calculateFee(LocalTime in, LocalTime out) {
        int hours = (minutesBetween(in, out) + 59) / 60;
        double fee = 0;
        for (int h = 0; h < hours; h++) {
            int startHour = in.plusHours(h).getHour();
            boolean peak = startHour >= 9 && startHour < 17;
            fee += peak ? 2 * ratePerHour() : ratePerHour();
        }
        return fee;
    }
}
