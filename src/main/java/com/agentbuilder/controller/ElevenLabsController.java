package com.agentbuilder.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agentbuilder.service.ElevenLabsService;

@RestController
@RequestMapping("/api/audio")
@CrossOrigin("*")
public class ElevenLabsController {
	
	@Autowired
	private ElevenLabsService elevenLabsService;
	
	@GetMapping(value = "/tts", produces = "audio/mpeg")
    public byte[] tts(@RequestParam String text,@RequestParam String voiceId) {
        return elevenLabsService.tts(text,voiceId);
    }


    @PostMapping("/stt")
    public Map<String,String> stt(@RequestPart MultipartFile file) {
        return Map.of("text", elevenLabsService.stt(file));
    }

}
