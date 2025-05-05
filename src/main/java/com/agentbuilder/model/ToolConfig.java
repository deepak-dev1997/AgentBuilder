package com.agentbuilder.model;

import lombok.Data;


import java.util.Map;

@Data
public class ToolConfig {
    private String toolName;
    private String description;
    private Map<String, String> headers;
    private String serverUrl;
    private Map<String,String> parameters;
    private Map<Object,Object> requestBody;
    private String beforeTool;
    private String afterTool;
}

