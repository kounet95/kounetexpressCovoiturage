package com.covoiturage.rideservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRideRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Driver name is required")
    private String driverName;

    @NotBlank(message = "Departure city is required")
    private String departureCity;

    @NotBlank(message = "Destination city is required")
    private String destinationCity;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "At least 1 seat required")
    @Max(value = 8, message = "Maximum 8 seats allowed")
    private Integer availableSeats;

    @NotNull(message = "Price per seat is required")
    @Positive(message = "Price must be positive")
    private Double pricePerSeat;

    private String vehicleMake;
    private String vehicleModel;
    private String description;
}
