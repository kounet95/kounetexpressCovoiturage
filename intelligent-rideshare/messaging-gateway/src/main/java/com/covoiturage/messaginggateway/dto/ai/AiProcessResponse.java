package com.covoiturage.messaginggateway.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProcessResponse {
    private String message;
    private List<QuickAction> quickActions;
    private List<ToolCall> toolCalls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuickAction {
        private String label;
        private String action;
        private String callbackData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolCall {
        private String name;
        private String arguments;
    }
}