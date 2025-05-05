package com.agentbuilder.dtos;

import lombok.Data;

@Data
public class ChatRequest {

    private String botId;
    private String message;
    private String conversationId;

}
