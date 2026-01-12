package com.covoiturage.messaginggateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ConversationContext {
    private String userId;
    private List<String> history = new ArrayList<>();
    private String currentIntent;
    private Object pendingData;
    private String platform; // optional: store platform if needed

    public ConversationContext(String userId) {
        this.userId = userId;
    }

    public void add(String userMsg, String botMsg) {
        history.add("U:" + userMsg);
        history.add("A:" + botMsg);
    }
}
