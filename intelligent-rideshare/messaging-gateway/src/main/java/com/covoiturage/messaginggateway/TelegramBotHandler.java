package com.covoiturage.messaginggateway;


import com.covoiturage.messaginggateway.dto.ai.AiProcessRequest;
import com.covoiturage.messaginggateway.dto.ai.AiProcessResponse;
import com.covoiturage.messaginggateway.dto.ai.MessageHistory;
import com.covoiturage.messaginggateway.service.AIAgentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBotHandler extends TelegramLongPollingBot {

    private final AIAgentClient aiAgentClient;
    private final String botUsername;

    public TelegramBotHandler(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            AIAgentClient aiAgentClient) {
        super(botToken);
        this.botUsername = botUsername;
        this.aiAgentClient = aiAgentClient;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
            }
        } catch (Exception e) {
            log.error("Error handling Telegram update", e);
            if (update.getMessage() != null) {
                sendErrorMessage(update.getMessage().getChatId());
            }
        }
    }

    private void handleTextMessage(Update update) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        String userId = update.getMessage().getFrom().getId().toString();
        String messageText = update.getMessage().getText();

        log.info("Received message from {}: {}", userId, messageText);

        // Construire une requête simple vers le service AI
        var history = new ArrayList<MessageHistory>();
        var ctx = AiProcessRequest.ConversationContext.builder()
                .history(history)
                .build();

        AiProcessRequest request = AiProcessRequest.builder()
                .userId(userId)
                .message(messageText)
                .context(ctx)
                .build();

        AiProcessResponse response = aiAgentClient.processMessage(request);

        // Envoyer la réponse
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(response.getMessage() != null ? response.getMessage() : "")
                .parseMode("HTML")
                .build();

        execute(message);
    }

    private void handleCallbackQuery(Update update) throws TelegramApiException {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String userId = update.getCallbackQuery().getFrom().getId().toString();
        String callbackData = update.getCallbackQuery().getData();

        log.info("Callback from {}: {}", userId, callbackData);

        // Traiter les actions des boutons
        switch (callbackData) {
            case "create_ride":
                sendMessage(chatId,
                        "🚗 Pour créer un trajet, dites-moi:\n" +
                                "- Ville de départ\n" +
                                "- Ville d'arrivée\n" +
                                "- Date et heure\n" +
                                "- Nombre de places disponibles\n" +
                                "- Prix par place\n\n" +
                                "Exemple: \"Je propose un trajet de Paris à Lyon le 15 janvier à 14h, 3 places à 25€\"");
                break;

            case "search_ride":
                sendMessage(chatId,
                        "🔍 Pour chercher un trajet, indiquez-moi:\n" +
                                "- Ville de départ\n" +
                                "- Ville d'arrivée\n" +
                                "- Date souhaitée\n" +
                                "- Nombre de passagers\n\n" +
                                "Exemple: \"Je cherche un trajet de Marseille à Nice demain pour 2 personnes\"");
                break;

            case "upload_documents":
                sendMessage(chatId,
                        "📄 Envoyez-moi vos documents avec une légende:\n" +
                                "- \"permis\" pour votre permis de conduire\n" +
                                "- \"assurance\" pour l'assurance du véhicule\n" +
                                "- \"carte grise\" pour la carte grise");
                break;

            default:
                // Action personnalisée (réservation, confirmation, etc.)
                handleCustomAction(chatId, userId, callbackData);
        }
    }

    private void sendWelcomeMessage(String chatId, String userId) throws TelegramApiException {
        String welcomeText = """
            🚗 <b>Bienvenue sur la plateforme de covoiturage intelligent!</b>
            
            Je suis votre assistant IA et je peux vous aider à:
            
            ✅ Proposer un trajet
            ✅ Chercher un trajet
            ✅ Réserver une place
            ✅ Gérer vos documents
            ✅ Suivre vos réservations
            
            💬 Parlez-moi naturellement, je comprends le français!
            
            <i>Exemple: "Je cherche un trajet de Paris à Lyon demain"</i>
            """;

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(welcomeText)
                .parseMode("HTML")
                .replyMarkup(createMainMenuKeyboard())
                .build();

        execute(message);
    }

    private void sendHelpMessage(String chatId) throws TelegramApiException {
        String helpText = """
            📖 <b>Guide d'utilisation</b>
            
            <b>Commandes disponibles:</b>
            /start - Menu principal
            /help - Afficher ce message
            /myrides - Voir mes trajets
            
            <b>Exemples de messages:</b>
            
            🚗 <b>Proposer un trajet:</b>
            "Je propose un trajet de Paris à Lyon le 20 janvier à 14h, 3 places à 25€"
            
            🔍 <b>Chercher un trajet:</b>
            "Je cherche un trajet de Marseille à Nice demain pour 2 personnes"
            
            📅 <b>Réserver:</b>
            "Je veux réserver 2 places pour le trajet #12345"
            
            💳 <b>Paiement:</b>
            Tous les paiements se font via la plateforme de manière sécurisée.
            """;

        sendMessage(chatId, helpText);
    }

    private InlineKeyboardMarkup createMainMenuKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Ligne 1
        keyboard.add(List.of(
                InlineKeyboardButton.builder()
                        .text("🚗 Proposer un trajet")
                        .callbackData("create_ride")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("🔍 Chercher un trajet")
                        .callbackData("search_ride")
                        .build()
        ));

        // Ligne 2
        keyboard.add(List.of(
                InlineKeyboardButton.builder()
                        .text("📄 Mes documents")
                        .callbackData("upload_documents")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("📊 Mes statistiques")
                        .callbackData("my_stats")
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private InlineKeyboardMarkup createQuickActionsKeyboard(List<String> actions) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String action : actions) {
            keyboard.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(action)
                            .callbackData("action_" + action.toLowerCase().replace(" ", "_"))
                            .build()
            ));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .build();
        execute(message);
    }

    private void sendErrorMessage(Long chatId) {
        try {
            sendMessage(chatId.toString(),
                    "❌ Désolé, une erreur s'est produite. Veuillez réessayer.");
        } catch (TelegramApiException e) {
            log.error("Failed to send error message", e);
        }
    }

    private String detectDocumentType(String caption) {
        if (caption == null) return "document";
        String lower = caption.toLowerCase();
        if (lower.contains("permis")) return "permis de conduire";
        if (lower.contains("assurance")) return "assurance";
        if (lower.contains("carte grise")) return "carte grise";
        return "document";
    }

    private void handleCustomAction(String chatId, String userId, String action) {
        // Implémenter les actions personnalisées
    }

    private void handleToolCallNotifications(String chatId, List<?> toolCalls) {
        // Envoyer des notifications pour les actions effectuées
    }

    private void sendUserRides(String chatId, String userId) {
        // Récupérer et afficher les trajets de l'utilisateur
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
