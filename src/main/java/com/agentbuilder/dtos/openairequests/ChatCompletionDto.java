package com.agentbuilder.dtos.openairequests;

import lombok.Data;
import java.util.List;

@Data
public class ChatCompletionDto {

    private String model;
    private List<Message> messages;
    private ResponseFormat response_format;
    private double temperature;


}
