package main.java.alex.falendish.service.impl;

import main.java.alex.falendish.dao.VehicleDAO;
import main.java.alex.falendish.dao.impl.JdbcVehicleDAO;
import main.java.alex.falendish.exception.InsufficientVehiclesNumberException;
import main.java.alex.falendish.model.Vehicle;
import main.java.alex.falendish.service.VehicleService;
import main.java.alex.falendish.utils.VehicleCategoryType;
import main.java.alex.falendish.utils.VehicleStatus;
import main.java.alex.falendish.utils.VehicleType;

import java.math.BigDecimal;
import java.util.*;

public class VehicleServiceImpl implements VehicleService {

    private final VehicleDAO vehicleDAO = new JdbcVehicleDAO();

    @Override
    public Collection<Vehicle> findAvailableVehicles(VehicleCategoryType categoryType, VehicleType vehicleType, int seats) {
        if (seats < 1) {
            throw new IllegalArgumentException("Number of booked seats should grater than 0");
        }
        Collection<VehicleType> suitableVehicleTypes = getSuitableVehicleTypes(vehicleType, seats);

        Collection<Vehicle> vehicles = new ArrayList<>(suitableVehicleTypes.size());
        suitableVehicleTypes
                .forEach(type -> vehicleDAO.findAvailableVehicle(categoryType, type).ifPresent(vehicles::add));
        if (vehicles.size() == suitableVehicleTypes.size()) {
            return vehicles;
        }
        throw new InsufficientVehiclesNumberException("Number of available cars is less than required");
    }

    @Override
    public Collection<Long> reserveVehicles(Collection<Vehicle> vehicles, long orderId, BigDecimal price) {
        if (vehicles == null || vehicles.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.emptySet();
    }

    @Override
    public void tryToCancelUnconfirmedBookedVehicles(Collection<Long> reservedVehicleIds) {
        vehicleDAO.tryToCancelUnconfirmedBookedVehicles(reservedVehicleIds);
    }

    @Override
    public void updateStatusByIds(VehicleStatus pending, Collection<Long> boundVehicleIds) {

    }

    @Override
    public void updateStatusByBookingId(VehicleStatus pending, Long bookingId) {

    }

    @Override
    public void sendBookingRequestToVehicles(Long bookingId) {

    }

    @Override
    public Collection<Vehicle> findAllByIds(Collection<Long> vehicleIds) {
        return null;
    }

    private static VehicleType getVehicleType(int seats) {
        return Arrays.stream(VehicleType.values())
                .sorted(Comparator.comparing(VehicleType::getSeats))
                .filter(v -> v.getSeats() >= seats)
                .findFirst()
                .orElse(VehicleType.BUS);
    }

    private static Collection<VehicleType> getSuitableVehicleTypes(VehicleType vehicleType, int seats) {
        Collection<VehicleType> vehicleTypes = new ArrayList<>();
        do {
            VehicleType type = vehicleType == null
                    ? getVehicleType(seats)
                    : vehicleType;
            vehicleTypes.add(type);
            if (seats > type.getSeats()) {
                seats -= type.getSeats();
            } else {
                seats = 0;
            }
        } while (seats > 0);
        return vehicleTypes;
    }
}
