package parking.service;

import parking.model.*;

public final class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String regNo) {
        if (type == null) throw new IllegalArgumentException("Vehicle type required");
        return switch (type.trim().toUpperCase()) {
            case "TWOWHEELER" -> new TwoWheeler(regNo);
            case "CAR" -> new Car(regNo);
            case "EV" -> new ElectricCar(regNo);
            case "EBIKE" -> new EBike(regNo);
            case "VAN" -> new DeliveryVan(regNo);
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
    }
}
