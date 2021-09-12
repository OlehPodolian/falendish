package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.Vehicle;
import main.java.alex.falendish.utils.VehicleCategoryType;
import main.java.alex.falendish.utils.VehicleStatus;
import main.java.alex.falendish.utils.VehicleType;

import java.util.Collection;
import java.util.Optional;

public interface VehicleDAO {

    Vehicle create(Vehicle vehicle);

    Optional<Vehicle> findAvailableVehicle(VehicleCategoryType categoryType, VehicleType type);

    void updateStatusByBookingId(VehicleStatus status, Long bookingId);

    void updateStatusByIds(VehicleStatus status, Collection<Long> boundVehicleIds);

    Collection<Vehicle> findAllByIds(Collection<Long> vehicleIds);
}
