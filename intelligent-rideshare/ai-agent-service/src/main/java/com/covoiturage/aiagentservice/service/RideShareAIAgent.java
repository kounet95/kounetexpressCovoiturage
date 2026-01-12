package com.covoiturage.aiagentservice.service;

import com.covoiturage.aiagentservice.dto.SearchRidesRequest;
import com.covoiturage.common.dto.ai.AiProcessRequest;
import com.covoiturage.common.dto.ai.AiProcessResponse;
import com.covoiturage.common.dto.ai.QuickAction;
import com.covoiturage.common.dtos.RideDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideShareAIAgent {

    private final ChatClient.Builder chatClientBuilder;
    private final RideToolService rideToolService;

    private static final String SYSTEM_PROMPT = """
        Tu es CovoitIA, un assistant intelligent pour une plateforme de covoiturage française.
        
        Tu dois aider les utilisateurs à:
        - Chercher des trajets disponibles
        - Proposer des trajets
        - Réserver des places
        - Gérer leurs documents
        
        RÈGLES IMPORTANTES:
        1. Toujours être courtois et professionnel
        2. Répondre en français
        3. Extraire les informations clés: ville de départ, destination, date, nombre de places
        4. Si une information manque, la demander poliment
        5. Formater les réponses de manière claire avec des emojis
        
        OUTILS DISPONIBLES:
        - searchRides: rechercher des trajets
        - createRide: créer un trajet
        - bookRide: réserver une place
        
        Utilise ces outils quand l'utilisateur exprime une intention claire.
        """;

    public AiProcessResponse processUserMessage(AiProcessRequest request) {
        log.info("Processing message for user {}: {}", request.getUserId(), request.getMessage());

        try {
            // Construire l'historique de conversation
            StringBuilder conversationHistory = new StringBuilder();
            if (request.getContext() != null && request.getContext().getHistory() != null) {
                request.getContext().getHistory().forEach(msg -> {
                    conversationHistory.append(msg.getRole())
                            .append(": ")
                            .append(msg.getContent())
                            .append("\n");
                });
            }

            // Ajouter le message actuel
            conversationHistory.append("user: ").append(request.getMessage());

            // Créer le ChatClient avec le système prompt
            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(SYSTEM_PROMPT)
                    .build();

            // Appeler Spring AI
            String aiResponse = chatClient.prompt()
                    .user(conversationHistory.toString())
                    .call()
                    .content();

            log.info("AI Response generated: {}", aiResponse);

            // Analyser si l'IA veut faire une recherche de trajets
            AiProcessResponse response = processAIResponse(aiResponse, request);

            return response;

        } catch (Exception e) {
            log.error("Error in AI processing", e);
            return AiProcessResponse.builder()
                    .message("Désolé, j'ai rencontré un problème. Pouvez-vous reformuler votre demande?")
                    .build();
        }
    }

    private AiProcessResponse processAIResponse(String aiResponse, AiProcessRequest request) {

        // Détecter l'intention dans la réponse
        String lowerResponse = aiResponse.toLowerCase();
        String userMessage = request.getMessage().toLowerCase();

        // Si l'utilisateur cherche un trajet
        if (userMessage.contains("cherche") || userMessage.contains("recherche") ||
                userMessage.contains("besoin")) {

            // Extraire les informations
            SearchRidesRequest searchRequest = extractSearchInfo(request.getMessage());

            if (searchRequest.getDeparture() != null && searchRequest.getDestination() != null) {
                // Appeler le service de recherche
                List<RideDTO> rides = rideToolService.searchRides(searchRequest);

                if (!rides.isEmpty()) {
                    String formattedResponse = formatRidesResponse(rides);

                    List<QuickAction> actions = new ArrayList<>();
                    for (int i = 0; i < Math.min(rides.size(), 3); i++) {
                        RideDTO ride = rides.get(i);
                        actions.add(QuickAction.builder()
                                .label("📍 " + ride.driverName() + " - " + ride.pricePerSeat() + "€")
                                .action("view_ride")
                                .callbackData("view_ride:" + ride.id())
                                .build());
                    }

                    return AiProcessResponse.builder()
                            .message(formattedResponse)
                            .quickActions(actions)
                            .build();
                } else {
                    return AiProcessResponse.builder()
                            .message("😔 Désolé, je n'ai trouvé aucun trajet correspondant à vos critères.\n\n" +
                                    "Voulez-vous:\n" +
                                    "• Modifier vos critères de recherche?\n" +
                                    "• Créer une alerte pour ce trajet?")
                            .build();
                }
            }
        }

        // Réponse par défaut de l'IA
        return AiProcessResponse.builder()
                .message(aiResponse)
                .build();
    }

    private SearchRidesRequest extractSearchInfo(String message) {
        SearchRidesRequest request = new SearchRidesRequest();

        // Extraction simplifiée - dans la vraie vie, utiliser NLP plus avancé
        String lower = message.toLowerCase();

        // Extraire les villes (simpliste)
        if (lower.contains("paris")) request.setDeparture("Paris");
        if (lower.contains("lyon")) request.setDestination("Lyon");
        if (lower.contains("marseille")) request.setDeparture("Marseille");
        if (lower.contains("nice")) request.setDestination("Nice");
        if (lower.contains("toulouse")) request.setDestination("Toulouse");
        if (lower.contains("bordeaux")) request.setDestination("Bordeaux");

        // Date
        if (lower.contains("demain")) {
            request.setDate(LocalDateTime.now().plusDays(1));
        } else if (lower.contains("aujourd'hui")) {
            request.setDate(LocalDateTime.now());
        } else {
            request.setDate(LocalDateTime.now().plusDays(1)); // par défaut
        }

        // Nombre de passagers
        request.setPassengers(1); // par défaut
        if (lower.contains("2 personnes") || lower.contains("deux personnes")) {
            request.setPassengers(2);
        }

        return request;
    }

    private String formatRidesResponse(List<RideDTO> rides) {
        StringBuilder response = new StringBuilder();
        response.append("🚗 <b>J'ai trouvé ").append(rides.size()).append(" trajet(s) disponible(s):</b>\n\n");

        int count = 1;
        for (RideDTO ride : rides) {
            response.append("<b>").append(count++).append(". ")
                    .append(ride.driverName()).append("</b> ⭐ ")
                    .append(String.format("%.1f", ride.driverRating()))
                    .append("\n");

            response.append("📍 ").append(ride.departure())
                    .append(" → ").append(ride.destination())
                    .append("\n");

            response.append("🕐 Départ: ")
                    .append(ride.departureTime().toLocalDate())
                    .append(" à ")
                    .append(ride.departureTime().toLocalTime())
                    .append("\n");

            response.append("💺 Places: ").append(ride.availableSeats())
                    .append(" • 💰 Prix: ").append(ride.pricePerSeat())
                    .append("€\n");

            if (ride.vehicle() != null) {
                response.append("🚙 ").append(ride.vehicle()).append("\n");
            }

            response.append("\n");
        }

        response.append("Cliquez sur un trajet pour plus de détails ou pour réserver!");

        return response.toString();
    }
}
