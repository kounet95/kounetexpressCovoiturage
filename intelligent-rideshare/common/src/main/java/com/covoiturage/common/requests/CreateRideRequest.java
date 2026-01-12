package com.covoiturage.common.requests;

import java.time.LocalDateTime;

public record CreateRideRequest(
        String driverId,
        String departure,
        String destination,
        LocalDateTime departureTime,
        Integer availableSeats,
        Double pricePerSeat,
        String vehicle,
        String description
) {}