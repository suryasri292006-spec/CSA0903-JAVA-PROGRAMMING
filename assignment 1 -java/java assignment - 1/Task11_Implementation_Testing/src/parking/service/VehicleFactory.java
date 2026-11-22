package parking.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

/**
 * Creates vehicles from a category name. Categories are kept in a registry,
 * so a new category is ADDED with register(...) and this class never has to be edited.
 */
public class VehicleFactory {
    private final Map<String, Function<String, Vehicle>> creators = new LinkedHashMap<>();

    public VehicleFactory() {
        register("TWOWHEELER", TwoWheeler::new);
        register("CAR", Car::new);
        register("EV", ElectricCar::new);
    }

    public void register(String category, Function<String, Vehicle> creator) {
        creators.put(category.trim().toUpperCase(), creator);
    }

    public Vehicle create(String category, String regNo) {
        if (category == null) {
            throw new IllegalArgumentException("Vehicle category is missing");
        }
        Function<String, Vehicle> creator = creators.get(category.trim().toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException("Unknown vehicle category: " + category);
        }
        return creator.apply(regNo);
    }

    public Set<String> supportedCategories() {
        return creators.keySet();
    }
}
