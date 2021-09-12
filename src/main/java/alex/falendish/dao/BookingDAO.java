package main.java.alex.falendish.dao;

import main.java.alex.falendish.model.Booking;
import main.java.alex.falendish.utils.BookingStatus;

import java.util.Collection;

public interface BookingDAO {

    Collection<Long> findAllBoundVehicleIds(Long bookingId);

    void updateBookingStatus(BookingStatus status, Long bookingId);

    Booking create(Booking booking);
}
