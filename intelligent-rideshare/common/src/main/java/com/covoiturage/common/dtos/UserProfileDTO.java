package com.covoiturage.common.dtos;

import java.util.List;

public  record UserProfileDTO(
        String id,
        String name,
        String phone,
        String email,
        Double rating,
        Integer totalRides,
        boolean isVerifiedDriver,
        List<String> verifiedDocuments
) {}