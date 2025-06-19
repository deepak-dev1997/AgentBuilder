package com.agentbuilder.dtos.openairequests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String conversationId;
    private String message;

    private String response;
    private String audio;
}
