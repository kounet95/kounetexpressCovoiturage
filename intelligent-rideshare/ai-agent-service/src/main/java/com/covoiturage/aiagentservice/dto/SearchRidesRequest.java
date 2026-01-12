package com.covoiturage.aiagentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRidesRequest {
    private String departure;
    private String destination;
    private LocalDateTime date;
    private Integer passengers;
}