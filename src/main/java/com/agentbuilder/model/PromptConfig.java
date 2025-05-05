package com.agentbuilder.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("prompt_configs")
public class PromptConfig {
    @Id
    private String botId;
    private String role;
    private String systemPrompt;
    private String firstMessage;
}
