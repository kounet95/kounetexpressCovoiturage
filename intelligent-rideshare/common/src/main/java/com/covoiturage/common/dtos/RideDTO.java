package com.covoiturage.common.dtos;

import java.time.LocalDateTime;

// ========== DTOs ==========
public record RideDTO(
        String id,
        String driverId,
        String driverName,
        Double driverRating,
        String departure,
        String destination,
        LocalDateTime departureTime,
        Integer availableSeats,
        Double pricePerSeat,
        String vehicle,
        String description
) {}
