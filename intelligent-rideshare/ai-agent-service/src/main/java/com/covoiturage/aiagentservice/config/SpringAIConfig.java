package com.covoiturage.aiagentservice.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    @Bean
    public ChatClient.Builder chatClientBuilder(ChatClient.Builder builder) {
        return builder;
    }
}
