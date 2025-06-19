package com.agentbuilder.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agentbuilder.dtos.ChatRequest;
import com.agentbuilder.dtos.openairequests.ChatResponse;
import com.agentbuilder.service.ElevenLabsService;
import com.agentbuilder.service.RagService;


@RestController
@RequestMapping("/api/rag")
@CrossOrigin("*")
public class RagController {

    @Autowired
    private RagService ragService;
    
    @Autowired
    private ElevenLabsService elevenLabsService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest,
                             @RequestParam(defaultValue = "false") boolean voice) {

        ChatResponse res = ragService.chat(chatRequest);

        if (voice && res.getMessage() != null && !res.getMessage().isBlank()) {
            byte[] mp3 = elevenLabsService.tts(res.getMessage());
            // simplest: Base-64 inline
            res.setAudio(Base64.getEncoder().encodeToString(mp3));
            // or store the mp3 temporarily and return a URL instead
        }
        return res;
    }

}

