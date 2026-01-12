package com.covoiturage.aiagentservice.mcp;

import com.covoiturage.aiagentservice.service.RideShareAIAgent;
import com.covoiturage.common.dto.ai.AiProcessRequest;
import com.covoiturage.common.dto.ai.AiProcessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.covoiturage.aiagentservice.mcp.McpDtos.*;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
public class McpController {

    private final RideShareAIAgent agent;

    @GetMapping("/tools")
    public ResponseEntity<List<ToolDescription>> listTools() {
        ToolDescription aiProcess = ToolDescription.builder()
                .name("ai_process")
                .description("Process a user message via the AI agent and return a response")
                .inputSchema(Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "userId", Map.of("type", "string"),
                                "message", Map.of("type", "string"),
                                "context", Map.of("type", "object")
                        ),
                        "required", List.of("userId", "message")
                ))
                .outputSchema(Map.of("type", "object"))
                .build();
        return ResponseEntity.ok(List.of(aiProcess));
    }

    @PostMapping(value = "/execute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExecuteResponse> execute(@RequestBody ExecuteRequest request) {
        try {
            if (request.getToolName() == null) {
                return ResponseEntity.badRequest().body(ExecuteResponse.builder().ok(false).error("toolName is required").build());
            }
            String name = request.getToolName();
            Map<String, Object> args = request.getArguments();
            if (args == null) args = Map.of();

            switch (name) {
                case "ai_process":
                    String userId = (String) args.get("userId");
                    String message = (String) args.get("message");
                    AiProcessRequest.ConversationContext ctx = AiProcessRequest.ConversationContext.builder()
                            .history(java.util.Collections.emptyList())
                            .build();
                    AiProcessRequest req = AiProcessRequest.builder()
                            .userId(userId)
                            .message(message)
                            .context(ctx)
                            .build();
                    AiProcessResponse resp = agent.processUserMessage(req);
                    return ResponseEntity.ok(ExecuteResponse.builder().ok(true).result(resp).build());
                default:
                    return ResponseEntity.badRequest().body(ExecuteResponse.builder().ok(false).error("Unknown tool: " + name).build());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ExecuteResponse.builder().ok(false).error(e.getMessage()).build());
        }
    }
}