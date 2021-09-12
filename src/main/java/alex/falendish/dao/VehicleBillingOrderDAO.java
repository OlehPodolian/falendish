package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.VehicleBillingOrder;
import main.java.alex.falendish.utils.VehicleBillingOrderStatus;

import java.util.Collection;

public interface VehicleBillingOrderDAO {

    VehicleBillingOrder save(VehicleBillingOrder order);

    Collection<VehicleBillingOrder> findAllByVehicleId(Long vehicleId);

    void updateStatusByBookingId(VehicleBillingOrderStatus status, Long bookingId);
}
