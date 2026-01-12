package com.covoiturage.aiagentservice.config;

import com.covoiturage.common.dtos.PriceCalculation;
import com.covoiturage.common.dtos.RideDTO;
import com.covoiturage.common.dtos.UserProfileDTO;
import com.covoiturage.common.requests.*;
import com.covoiturage.common.response.BookingResponse;
import com.covoiturage.common.response.DriverVerificationResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class AIToolsConfiguration {

    // ========== RECHERCHE DE TRAJETS ==========

    @Bean
    @Description("Recherche des trajets disponibles selon les critères: ville de départ, ville d'arrivée, date")
    public Function<SearchRideRequest, List<RideDTO>> searchRides(RideService rideService) {
        return request -> {
            return rideService.searchRides(
                    request.departure(),
                    request.destination(),
                    request.date(),
                    request.passengers()
            );
        };
    }

    // ========== CRÉATION DE TRAJET ==========

    @Bean
    @Description("Crée un nouveau trajet. Vérifie automatiquement les documents du chauffeur")
    public Function<CreateRideRequest, RideCreationResponse> createRide(
            RideService rideService,
            DocumentService documentService) {
        return request -> {
            // Vérifier les documents du chauffeur
            var verification = documentService.verifyDriverDocuments(request.driverId());

            if (!verification.isValid()) {
                return RideCreationResponse.failed(
                        "Documents invalides: " + verification.getMissingDocuments()
                );
            }

            // Créer le trajet
            var ride = rideService.createRide(request);

            return RideCreationResponse.success(
                    ride.getId(),
                    "Trajet créé avec succès! ID: " + ride.getId()
            );
        };
    }
    // ========== RÉSERVATION ==========

    @Bean
    @Description("Réserve une place dans un trajet existant")
    public Function<BookRideRequest, BookingResponse> bookRide(
            BookingService bookingService,
            RideService rideService) {
        return request -> {
            // Vérifier la disponibilité
            var ride = rideService.getRideById(request.rideId());

            if (ride.getAvailableSeats() < request.seats()) {
                return BookingResponse.failed("Pas assez de places disponibles");
            }

            // Créer la réservation
            var booking = bookingService.createBooking(
                    request.userId(),
                    request.rideId(),
                    request.seats()
            );

            return BookingResponse.success(
                    booking.getId(),
                    "Réservation confirmée! Montant: " + booking.getTotalPrice() + "€"
            );
        };
    }

    // ========== VÉRIFICATION CHAUFFEUR ==========

    @Bean
    @Description("Vérifie les documents d'un chauffeur (permis, assurance, véhicule)")
    public Function<VerifyDriverRequest, DriverVerificationResult> verifyDriver(
            DocumentService documentService) {
        return request -> {
            return documentService.verifyDriverDocuments(request.driverId());
        };
    }


    // ========== PROFIL UTILISATEUR ==========

    @Bean
    @Description("Récupère le profil complet d'un utilisateur")
    public Function<GetUserProfileRequest, UserProfileDTO> getUserProfile(
            UserService userService) {
        return request -> {
            return userService.getUserProfile(request.userId());
        };
    }

    record GetUserProfileRequest(String userId) {}



    // ========== CALCUL DE PRIX ==========

    @Bean
    @Description("Calcule le prix suggéré pour un trajet basé sur la distance et les coûts")
    public Function<CalculatePriceRequest, PriceCalculation> calculatePrice(
            RideService rideService) {
        return request -> {
            double distance = rideService.calculateDistance(
                    request.departure(),
                    request.destination()
            );

            // Calcul: (essence + péages + usure) / nombre de passagers
            double fuelCost = distance * 0.08; // 8 centimes/km
            double tollCost = request.hasTolls() ? 15.0 : 0.0;
            double wearCost = distance * 0.05; // 5 centimes/km usure

            double totalCost = fuelCost + tollCost + wearCost;
            double pricePerSeat = totalCost / (request.passengers() + 1); // +1 pour le chauffeur

            return new PriceCalculation(
                    distance,
                    pricePerSeat,
                    totalCost,
                    "Prix suggéré: " + String.format("%.2f", pricePerSeat) + "€ par passager"
            );
        };
    }



}


