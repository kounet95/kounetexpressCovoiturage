package com.covoiturage.messaginggateway.service;


import com.covoiturage.messaginggateway.dto.ai.AiProcessRequest;
import com.covoiturage.messaginggateway.dto.ai.AiProcessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAgentClient {

    private final RestTemplate restTemplate;

    @Value("${services.ai-agent.url}")
    private String aiAgentUrl;

    public AiProcessResponse processMessage(AiProcessRequest request) {
        try {
            log.info("Calling AI Agent Service: {}", aiAgentUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AiProcessRequest> entity = new HttpEntity<>(request, headers);

            String url = aiAgentUrl + "/api/ai/process";

            AiProcessResponse response = restTemplate.postForObject(
                    url,
                    entity,
                    AiProcessResponse.class
            );

            log.info("AI Agent response received successfully");
            return response;

        } catch (Exception e) {
            log.error("Error calling AI Agent Service", e);
            return AiProcessResponse.builder()
                    .message("Désolé, je rencontre un problème technique. Veuillez réessayer.")
                    .build();
        }
    }
}

