package com.covoiturage.aiagentservice.ai.agent;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideShareAIAgent {

    private final ChatClient chatClient;
    private final RideToolService rideToolService;
    private final BookingToolService bookingToolService;
    private final UserToolService userToolService;

    private static final String SYSTEM_PROMPT = """
        Tu es un assistant intelligent pour une plateforme de covoiturage.
        Tu aides les utilisateurs à:
        - Proposer des trajets
        - Chercher des trajets disponibles
        - Réserver des places
        - Gérer leurs documents (permis, assurance)
        - Obtenir des informations sur leurs réservations
        
        Règles importantes:
        1. Toujours vérifier l'identité de l'utilisateur avant toute action
        2. Être clair sur les conditions légales et de sécurité
        3. Ne jamais permettre de paiement en cash - tout passe par la plateforme
        4. Vérifier que le chauffeur a un permis valide et une assurance
        5. Être courtois et professionnel
        
        Tu disposes des outils suivants:
        - searchRides: rechercher des trajets
        - createRide: créer un nouveau trajet
        - bookRide: réserver une place
        - verifyDriver: vérifier les documents d'un chauffeur
        - getUserProfile: obtenir le profil utilisateur
        - calculatePrice: calculer le prix d'un trajet
        
        Réponds toujours en français de manière naturelle et conviviale.
        """;

    public AgentResponse processUserMessage(String userId, String message, ConversationContext context) {
        try {
            log.info("Processing message from user {}: {}", userId, message);

            // Construire le contexte de la conversation
            Map<String, Object> contextData = Map.of(
                    "userId", userId,
                    "conversationHistory", context.getHistory(),
                    "userProfile", userToolService.getUserProfile(userId)
            );

            // Créer le prompt avec le contexte
            Message systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT)
                    .createMessage(contextData);
            Message userMessage = new UserMessage(message);

            // Appeler le modèle avec les tools disponibles
            var response = chatClient.prompt()
                    .messages(systemMessage, userMessage)
                    .functions("searchRides", "createRide", "bookRide",
                            "verifyDriver", "getUserProfile", "calculatePrice")
                    .call()
                    .chatResponse();

            // Extraire la réponse et les actions
            String responseText = response.getResult().getOutput().getContent();
            List<ToolCall> toolCalls = extractToolCalls(response);

            // Exécuter les outils appelés
            AgentResponse agentResponse = AgentResponse.builder()
                    .message(responseText)
                    .toolCalls(toolCalls)
                    .build();

            // Mettre à jour le contexte
            context.addMessage(message, responseText);

            return agentResponse;

        } catch (Exception e) {
            log.error("Error processing message", e);
            return AgentResponse.builder()
                    .message("Désolé, j'ai rencontré un problème. Pouvez-vous reformuler votre demande?")
                    .error(true)
                    .build();
        }
    }

    private List<ToolCall> extractToolCalls(org.springframework.ai.chat.model.ChatResponse response) {
        // Extraire les appels d'outils depuis la réponse
        return response.getResults().stream()
                .flatMap(r -> r.getOutput().getToolCalls().stream())
                .map(tc -> ToolCall.builder()
                        .name(tc.name())
                        .arguments(tc.arguments())
                        .build())
                .toList();
    }
}

@lombok.Data
@lombok.Builder
class AgentResponse {
    private String message;
    private List<ToolCall> toolCalls;
    private boolean error;
}

@lombok.Data
@lombok.Builder
class ToolCall {
    private String name;
    private Map<String, Object> arguments;
}
