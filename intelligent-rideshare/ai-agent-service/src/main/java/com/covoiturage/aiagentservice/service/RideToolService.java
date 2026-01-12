package com.covoiturage.aiagentservice.service;

import com.covoiturage.aiagentservice.dto.SearchRidesRequest;
import com.covoiturage.common.dtos.RideDTO;

import java.util.List;

public interface RideToolService {
    List<RideDTO> searchRides(SearchRidesRequest request);
}