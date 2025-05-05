package com.agentbuilder.controller;

import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.ToolConfig;
import com.agentbuilder.service.ToolManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bots/{botName}/tools")
@CrossOrigin("*")
public class ToolConfigController {
    @Autowired
    private ToolManagementService service;



    @GetMapping
    public List<ToolConfig> listTools(@PathVariable String botName) {
        return service.listTools(botName);
    }

    @GetMapping("/{toolName}")
    public ToolConfig getTool(
            @PathVariable String botName,
            @PathVariable String toolName
    ) {
        return service.getTool(botName, toolName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BotConfig createTool(@PathVariable String botName, @RequestBody ToolConfig tool, UriComponentsBuilder uriBuilder) {
       return service.addTool(botName, tool);

    }

    @PutMapping("/{toolName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTool(
            @PathVariable String botName,
            @PathVariable String toolName,
            @RequestBody ToolConfig tool
    ) {
        service.updateTool(botName, toolName, tool);
    }

    @DeleteMapping("/{toolName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTool(
            @PathVariable String botName,
            @PathVariable String toolName
    ) {
        service.deleteTool(botName, toolName);
    }
}

