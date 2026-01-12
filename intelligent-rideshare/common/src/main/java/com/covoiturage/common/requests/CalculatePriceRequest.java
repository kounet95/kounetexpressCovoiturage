package com.covoiturage.common.requests;

public  record CalculatePriceRequest(
        String departure,
        String destination,
        Integer passengers,
        boolean hasTolls
) {}