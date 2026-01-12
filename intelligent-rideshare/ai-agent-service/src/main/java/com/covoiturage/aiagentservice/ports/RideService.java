package com.covoiturage.aiagentservice.ports;

import com.covoiturage.common.dtos.RideDTO;
import com.covoiturage.common.requests.CreateRideRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface RideService {
    List<RideDTO> searchRides(String departure, String destination, LocalDateTime date, Integer passengers);
    RideDTO createRide(CreateRideRequest request);
    RideDTO getRideById(String rideId);
    double calculateDistance(String departure, String destination);
}