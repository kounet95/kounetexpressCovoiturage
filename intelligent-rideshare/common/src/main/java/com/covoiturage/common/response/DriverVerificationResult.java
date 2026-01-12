package com.covoiturage.common.response;

import java.util.List;

public  record DriverVerificationResult(
        boolean isValid,
        boolean hasValidLicense,
        boolean hasValidInsurance,
        boolean hasVehicleDocuments,
        List<String> missingDocuments,
        String message
) {}