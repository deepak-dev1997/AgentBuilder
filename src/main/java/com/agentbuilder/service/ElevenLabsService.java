package com.agentbuilder.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ElevenLabsService {

    @Value("${eleven.api.key}")
    private String apiKey;
    @Value("${eleven.voice}")
    private String voiceId;
    @Value("${eleven.ttsModel}")
    private String ttsModelId;
    @Value("${eleven.sttModel}")
    private String sttModelId;

    private static final String BASE_URL = "https://api.elevenlabs.io/v1";

    private final RestTemplate restTemplate;

    public ElevenLabsService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /* ---------- T T S ---------- */
    public byte[] tts(String text) {
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.valueOf("audio/mpeg")));
        headers.set("xi-api-key", apiKey);

        // request body
        Map<String, String> body = Map.of(
                "text", text,
                "model_id", ttsModelId
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        String url = BASE_URL + "/text-to-speech/" + voiceId + "?output_format=mp3_44100_128";

        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, entity, byte[].class);
        return response.getBody();
    }

    /* ---------- S T T ---------- */
    public String stt(MultipartFile audio) {
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("xi-api-key", apiKey);

        // multipart parts
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        Resource audioResource = audio.getResource();
        form.add("file", audioResource);
        form.add("model_id", sttModelId);
        form.add("language_code", "eng");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(form, headers);

        String url = BASE_URL + "/speech-to-text";

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
        return response.getBody().get("text").asText();
    }
}
