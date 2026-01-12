package com.covoiturage.common.requests;

public record BookRideRequest(
        String userId,
        String rideId,
        Integer seats
) {}
