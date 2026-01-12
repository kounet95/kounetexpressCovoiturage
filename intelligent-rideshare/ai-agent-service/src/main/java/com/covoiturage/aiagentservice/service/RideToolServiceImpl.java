package com.covoiturage.aiagentservice.service;

import com.covoiturage.aiagentservice.dto.SearchRidesRequest;
import com.covoiturage.common.dtos.RideDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RideToolServiceImpl implements RideToolService {
    @Override
    public List<RideDTO> searchRides(SearchRidesRequest request) {
        // TODO: brancher vers ride-service (OpenFeign) ou repository local
        return Collections.emptyList();
    }
}