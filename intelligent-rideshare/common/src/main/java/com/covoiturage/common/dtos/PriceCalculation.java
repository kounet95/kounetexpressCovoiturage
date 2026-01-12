package com.covoiturage.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculation {
    private double distanceKm;
    private double pricePerSeat;
    private double totalCost;
    private String message;
}