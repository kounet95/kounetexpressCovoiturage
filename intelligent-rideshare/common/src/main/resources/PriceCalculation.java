package com.covoiturage.common.dtos;

public   record PriceCalculation(
        double distance,
        double pricePerSeat,
        double totalCost,
        String message
) {}
