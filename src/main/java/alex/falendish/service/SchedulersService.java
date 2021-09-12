package main.java.alex.falendish.service;

import java.util.Collection;

public interface SchedulersService {

    void scheduleUnconfirmedBookingCancellation(Long bookingId, Collection<Long> reservedVehicleIds, Long timeoutMs);

    void scheduleStartRideUserNotification(Collection<Long> vehicleIds);
}
