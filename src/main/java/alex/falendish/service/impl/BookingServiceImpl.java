package main.java.alex.falendish.service.impl;

import main.java.alex.falendish.dao.BookingDAO;
import main.java.alex.falendish.dao.BookingRequestDAO;
import main.java.alex.falendish.dao.PromoCodeDAO;
import main.java.alex.falendish.dao.impl.JdbcBookingDAO;
import main.java.alex.falendish.dao.impl.JdbcBookingRequestDAO;
import main.java.alex.falendish.dao.impl.JdbcPromoCodeDAO;
import main.java.alex.falendish.exception.InsufficientVehiclesNumberException;
import main.java.alex.falendish.model.*;
import main.java.alex.falendish.service.BookingService;
import main.java.alex.falendish.service.SchedulersService;
import main.java.alex.falendish.service.UserService;
import main.java.alex.falendish.service.VehicleService;
import main.java.alex.falendish.utils.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static main.java.alex.falendish.utils.CommonUtils.*;

public class BookingServiceImpl implements BookingService {

    private static final int BONUS_RIDES_COUNT = 10;
    private static final long AUTO_CANCELLATION_TIMEOUT = TimeUnit.MINUTES.toMillis(5L);

    private final BookingDAO bookingDAO = new JdbcBookingDAO();
    private final BookingRequestDAO bookingRequestDAO = new JdbcBookingRequestDAO();
    private final PromoCodeDAO promoCodeDAO = new JdbcPromoCodeDAO();
    private final VehicleService vehicleService = new VehicleServiceImpl();
    private final SchedulersService schedulersService = new SchedulersServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    public Booking book(BookingRequest request) {
        request = bookingRequestDAO.insert(request);
        long bookingId = System.currentTimeMillis();
        BigDecimal routePrice = calculateRoutePrice(request);
        Collection<Vehicle> primaryVehicles = new ArrayList<>();
        Collection<Vehicle> optionalVehicles = new ArrayList<>();
        try {
            primaryVehicles.addAll(vehicleService.findAvailableVehicles(request.getCategoryType(), request.getVehicleType(), request.getSeats()));
        } catch (InsufficientVehiclesNumberException e) {
            VehicleCategoryType categoryType = CommonUtils.getNextVehicleCategory(request.getCategoryType());
            while (categoryType != null) {
                try {
                    optionalVehicles.clear();
                    optionalVehicles.addAll(vehicleService.findAvailableVehicles(request.getCategoryType(), request.getVehicleType(), request.getSeats()));
                    categoryType = null;
                } catch (InsufficientVehiclesNumberException ex) {
                    categoryType = CommonUtils.getNextVehicleCategory(categoryType);
                }
            }
        }
        Collection<Long> reservedVehicleIds = Stream.concat(primaryVehicles.stream(), optionalVehicles.stream())
                .map(Vehicle::getId)
                .collect(Collectors.toSet());

        BigDecimal totalCost = calculateTotalPrice(primaryVehicles, optionalVehicles, routePrice);
        Discount discount = calculateDiscount(request, totalCost);

        BigDecimal totalPrice = totalCost.subtract(zeroIfNull(discount.getDiscount()));

        schedulersService.scheduleUnconfirmedBookingCancellation(bookingId, reservedVehicleIds, AUTO_CANCELLATION_TIMEOUT);

        Booking booking = bookingDAO.create(new Booking(
                bookingId,
                request.getUserId(),
                request.getId(),
                primaryVehicles,
                optionalVehicles,
                totalCost,
                totalPrice,
                zeroIfNull(discount.getDiscount()),
                discount.getDiscountType(),
                request.getPromoCode(),
                BookingStatus.OFFERED));
        vehicleService.reserveVehicles(primaryVehicles, bookingId, routePrice);
        vehicleService.reserveVehicles(optionalVehicles, bookingId, routePrice);
        return booking;
    }

    @Override
    public void confirmBooking(Long bookingId) {
        Collection<Long> boundVehicleIds = bookingDAO.findAllBoundVehicleIds(bookingId);
        bookingDAO.updateBookingStatus(BookingStatus.CONFIRMED, bookingId);
        vehicleService.updateStatusByIds(VehicleStatus.PENDING, boundVehicleIds);
        vehicleService.sendBookingRequestToVehicles(bookingId);
        schedulersService.scheduleStartRideUserNotification(boundVehicleIds);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        updateStatus(bookingId, BookingStatus.CANCELLED, VehicleStatus.IDLE);
    }

    @Override
    public void startRide(Long bookingId) {
        vehicleService.activateBillingOrders(bookingId);
        updateStatus(bookingId, BookingStatus.PROCESSING, VehicleStatus.RENTED);
    }

    @Override
    public void terminateRide(Long bookingId) {
        updateStatus(bookingId, BookingStatus.COMPLETED, VehicleStatus.IDLE);
    }

    private Discount calculateDiscount(BookingRequest request, BigDecimal totalCost) {
        int ridesCount = userService.calculateUserRides(request.getUserId());
        Discount discount = new Discount();
        if (ridesCount > 0 && ridesCount % BONUS_RIDES_COUNT == 0) {
            discount.setDiscountType(DiscountType.BONUS_RIDE);
            discount.setDiscount(totalCost.multiply(DiscountType.BONUS_RIDE.getPercent()).divide(BigDecimal.valueOf(100), RoundingMode.DOWN));
            return discount;
        } else if (isNotBlank(request.getPromoCode())) {
            promoCodeDAO.findOneActive(request.getPromoCode())
                    .ifPresent(promoCode -> {
                        discount.setPromoCode(promoCode.getValue());
                        discount.setDiscountType(DiscountType.PROMO_CODE);
                        discount.setDiscount(totalCost.multiply(promoCode.getPercent()).divide(BigDecimal.valueOf(100), RoundingMode.DOWN));
                    });
        }
        return discount;
    }

    private BigDecimal calculateTotalPrice(Collection<Vehicle> primaryVehicles, Collection<Vehicle> optionalVehicles, BigDecimal routePrice) {
        Collection<Vehicle> vehicles = new ArrayList<>();
        if (Objects.nonNull(primaryVehicles) && !primaryVehicles.isEmpty()) {
            vehicles.addAll(primaryVehicles);
        }
        if (Objects.nonNull(optionalVehicles) && !optionalVehicles.isEmpty()) {
            vehicles.addAll(optionalVehicles);
        }
        return vehicles
                .stream()
                .map(v -> v.getBookingPrice(routePrice))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateStatus(Long bookingId, BookingStatus cancelled, VehicleStatus idle) {
        bookingDAO.updateBookingStatus(cancelled, bookingId);
        vehicleService.updateStatusByBookingId(idle, bookingId);
    }
}
