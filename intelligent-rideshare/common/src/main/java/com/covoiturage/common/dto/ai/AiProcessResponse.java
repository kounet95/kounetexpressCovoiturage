package com.covoiturage.common.dto.ai;

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
}