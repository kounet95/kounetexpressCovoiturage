package com.covoiturage.rideservice.emun;

public enum RideStatus {
    DRAFT,          // En cours de création
    PUBLISHED,      // Publié et recherchable
    IN_PROGRESS,    // Trajet en cours
    COMPLETED,      // Trajet terminé
    CANCELLED       // Annulé
}
