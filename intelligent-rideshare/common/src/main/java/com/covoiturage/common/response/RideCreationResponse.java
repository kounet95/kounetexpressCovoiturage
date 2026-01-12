package com.covoiturage.common.response;

public record RideCreationResponse(
        boolean success,
        String rideId,
        String message
) {
    static RideCreationResponse success(String rideId, String message) {
        return new RideCreationResponse(true, rideId, message);
    }

    static RideCreationResponse failed(String message) {
        return new RideCreationResponse(false, null, message);
    }
}
