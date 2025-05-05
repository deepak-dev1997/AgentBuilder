package com.agentbuilder.controller;

import com.agentbuilder.dtos.ChatRequest;
import com.agentbuilder.dtos.openairequests.ChatResponse;
import com.agentbuilder.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/rag")
@CrossOrigin("*")
public class RagController {

    @Autowired
    private RagService ragService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest){
        return ragService.chat(chatRequest);

    }

}

