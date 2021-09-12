package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.Vehicle;
import main.java.alex.falendish.utils.VehicleCategoryType;
import main.java.alex.falendish.utils.VehicleType;

import java.util.Collection;
import java.util.Optional;

public interface VehicleDAO {

    Vehicle create(Vehicle vehicle);

    Optional<Vehicle> findAvailableVehicle(VehicleCategoryType categoryType, VehicleType type);

    void tryToCancelUnconfirmedBookedVehicles(Collection<Long> reservedVehicleIds);
}
