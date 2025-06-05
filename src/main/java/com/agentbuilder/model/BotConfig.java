package com.agentbuilder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("bot_configs")
public class BotConfig {


    @Id
    private String botName;
    private String botDescription;
    private List<ToolConfig> tools;
    private KnowledgeBase knowledgeBase;
    private String themeColor;   
    private String textColor;     
    private String textFont;      
    private String avatarUrl; 
    private String welcomeMsg;
    
}
