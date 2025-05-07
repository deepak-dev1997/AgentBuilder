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
    private Map<String,String> requestBody;
    private String beforeTool;
    private String afterTool;


    @Override
    public String toString() {
        return "ToolConfig{" +
                "toolName='" + toolName + '\'' +
                ", description='" + description + '\'' +
                ", headers=" + headers +
                ", serverUrl='" + serverUrl + '\'' +
                ", parameters=" + parameters +
                ", requestBody=" + requestBody +
                ", beforeTool='" + beforeTool + '\'' +
                ", afterTool='" + afterTool + '\'' +
                '}';
    }
}

