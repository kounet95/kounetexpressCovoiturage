package com.covoiturage.messaginggateway.dto.ai;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageHistory {
    private String role; // "user" ou "assistant"
    private String content;
    private LocalDateTime timestamp;
}

