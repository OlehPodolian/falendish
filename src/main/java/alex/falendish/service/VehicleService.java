package main.java.alex.falendish.service;

import main.java.alex.falendish.model.Vehicle;
import main.java.alex.falendish.utils.VehicleCategoryType;
import main.java.alex.falendish.utils.VehicleStatus;
import main.java.alex.falendish.utils.VehicleType;

import java.math.BigDecimal;
import java.util.Collection;

public interface VehicleService {

    Collection<Vehicle> findAvailableVehicles(VehicleCategoryType categoryType, VehicleType vehicleType, int seats);

    Collection<Long> reserveVehicles(Collection<Vehicle> vehicles, long orderId, BigDecimal price);

    void tryToCancelUnconfirmedBookedVehicles(Collection<Long> reservedVehicleIds);

    void updateStatusByIds(VehicleStatus pending, Collection<Long> boundVehicleIds);

    void updateStatusByBookingId(VehicleStatus pending, Long bookingId);

    void sendBookingRequestToVehicles(Long bookingId);

    Collection<Vehicle> findAllByIds(Collection<Long> vehicleIds);
}
