package com.covoiturage.aiagentservice.mcp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public class McpDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolDescription {
        private String name;
        private String description;
        private Map<String, Object> inputSchema;   // JSON Schema like structure (simplified)
        private Map<String, Object> outputSchema;  // JSON Schema like structure (simplified)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteRequest {
        private String toolName;
        private Map<String, Object> arguments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteResponse {
        private boolean ok;
        private Object result; // could be List or DTO
        private String error;
    }
}