package com.agentbuilder.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("llm_configs")
public class LLMConfig {
    @Id
    private String botId;
    private String model;
    private double temperature;
    private int maxTokens;
}

