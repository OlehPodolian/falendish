package main.java.alex.falendish.model;

import main.java.alex.falendish.utils.VehicleOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleOrder {

    private Long orderId;
    private Long vehicleId;
    private BigDecimal price;

    private VehicleOrderStatus orderStatus;

    private LocalDateTime created;
    private LocalDateTime modified;
}
