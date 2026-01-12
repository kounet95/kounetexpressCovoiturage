package com.covoiturage.aiagentservice.web;

import com.covoiturage.aiagentservice.service.RideShareAIAgent;
import com.covoiturage.common.dto.ai.AiProcessRequest;
import com.covoiturage.common.dto.ai.AiProcessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final RideShareAIAgent aiAgent;

    @PostMapping("/process")
    public ResponseEntity<AiProcessResponse> processMessage(
            @RequestBody AiProcessRequest request) {

        log.info("Processing AI request from user: {}", request.getUserId());
        log.debug("Message: {}", request.getMessage());

        try {
            AiProcessResponse response = aiAgent.processUserMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing AI request", e);
            return ResponseEntity.ok(AiProcessResponse.builder()
                    .message("Désolé, je rencontre un problème. Pouvez-vous reformuler?")
                    .build());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Agent Service is running");
    }
}

