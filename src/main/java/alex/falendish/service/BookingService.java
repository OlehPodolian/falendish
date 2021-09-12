package main.java.alex.falendish.service;

import main.java.alex.falendish.model.BookingRequest;
import main.java.alex.falendish.model.Booking;

public interface BookingService {

    Booking book(BookingRequest request);

    void confirmBooking(Long bookingId);

    void cancelBooking(Long bookingId);

    void startRide(Long bookingId);

    void terminateRide(Long bookingId);

}
