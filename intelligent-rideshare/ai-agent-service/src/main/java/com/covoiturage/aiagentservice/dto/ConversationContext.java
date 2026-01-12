package com.covoiturage.aiagentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {
    private String userId;
    private String platform;

    @Builder.Default
    private List<MessageHistory> history = new ArrayList<>();

    private String currentIntent;
    private Object pendingData;
}

