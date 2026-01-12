package com.covoiturage.common.requests;

import java.time.LocalDateTime;

public record SearchRideRequest(
        String departure,
        String destination,
        LocalDateTime date,
        Integer passengers
) {}
