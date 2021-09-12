package main.java.alex.falendish;

import main.java.alex.falendish.model.BookingRequest;
import main.java.alex.falendish.service.BookingService;
import main.java.alex.falendish.service.impl.BookingServiceImpl;
import main.java.alex.falendish.utils.VehicleCategoryType;

public class Main {

    public static void main(String[] args) {
        BookingService bookingService = new BookingServiceImpl();

        BookingRequest request = new BookingRequest();
        request.setUserId(4L);
        request.setStartAddress("Street #1");
        request.setDestinationAddress("Street #2");
        request.setCategoryType(VehicleCategoryType.STANDARD);
        request.setSeats(3);
        request.setPromoCode("TEST");
        System.out.println(bookingService.book(request));
    }
}
