package com.covoiturage.aiagentservice.service;

import com.covoiturage.aiagentservice.dto.SearchRidesRequest;
import com.covoiturage.common.dtos.RideDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAgentMcpTools {

    private final RideToolService rideToolService;

    @McpTool(description = "Recherche des trajets disponibles entre deux villes à une date donnée")
    public List<RideDTO> searchRides(
            @McpArg(description = "Ville de départ") String departure,
            @McpArg(description = "Ville d'arrivée") String destination,
            @McpArg(description = "Date/heure ISO-8601, ex: 2026-01-15T14:00 (optionnel)") String dateTime,
            @McpArg(description = "Nombre de passagers (optionnel)") Integer passengers
    ) {
        LocalDateTime when = null;
        if (dateTime != null && !dateTime.isBlank()) {
            try {
                when = LocalDateTime.parse(dateTime);
            } catch (DateTimeParseException e) {
                log.warn("Invalid dateTime format '{}', expected ISO-8601, ignoring", dateTime);
            }
        }
        SearchRidesRequest req = new SearchRidesRequest();
        req.setDeparture(departure);
        req.setDestination(destination);
        req.setDate(when);
        req.setPassengers(passengers);
        return rideToolService.searchRides(req);
    }
}
