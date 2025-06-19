package com.agentbuilder.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.agentbuilder.model.BotConfig;
import com.agentbuilder.repository.BotRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/widget")               
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WidgetController {
	
	private final BotRepository repo;
	
	@GetMapping(value = "/{botId}.js", produces = "application/javascript")
	public ResponseEntity<String> widgetJs(@PathVariable String botId,
	                                       HttpServletRequest req) {

	    BotConfig cfg = repo.findById(botId)
	                        .orElseThrow(() -> new ResponseStatusException(
	                                HttpStatus.NOT_FOUND, "Unknown bot"));

	    String baseUrl = ServletUriComponentsBuilder.fromRequest(req)
	                                                .replacePath(null)
	                                                .build()
	                                                .toUriString();

	    String js = """
	        (function () {
	          window.AgentBuilder = {
	            botId: "%s",
	            botName: "%s",
	            themeColor: "%s",
	            textColor: "%s",
	            textFont: "%s",
	            botAvatar: "%s",
	            welcomeMessage: "%s",
	            apiUrl:"%s",
	            ttsUrl: "%s",
	            sttUrl: "%s"
	          };
	          var s = document.createElement('script');
	          s.src = '%s/widget-core.js';
	          s.async = true;
	          document.head.appendChild(s);
	        })();
	        """.formatted(
	           cfg.getBotName(),          // use the real ID here
	           cfg.getBotName(),
	           cfg.getThemeColor(),
	           cfg.getTextColor(),
	           cfg.getTextFont(),
	           cfg.getAvatarUrl(),
	           cfg.getWelcomeMsg(),
	           baseUrl+"/api/rag/chat",
	           baseUrl+"/api/audio/tts",
	           baseUrl+"/api/audio/stt",
	           baseUrl
	        );

	    return ResponseEntity.ok(js);
	}

	    /* ------------------------------------------------------------------
	     * 2️⃣  NEW: returns the <script> tag
	     *      GET  /widget/{botId}
	     * ------------------------------------------------------------------ */
	    @GetMapping(value = "/{botId}", produces = "text/html; charset=UTF-8")
	    public ResponseEntity<String> widgetTag(@PathVariable String botId,
	                                            HttpServletRequest req) {

	        /* just validate the bot exists – we don’t need the details */
	        if (!repo.existsById(botId)) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown bot");
	        }

	        /* Build absolute URL to the .js (handles reverse-proxy, context-path, etc.) */
	        String baseUrl = ServletUriComponentsBuilder.fromRequest(req)
	                                                    .replacePath(null)
	                                                    .build()
	                                                    .toUriString();
	        String scriptUrl = baseUrl + "/widget/" + botId + ".js";

	        String tag = "<script src=\"" + scriptUrl + "\" defer></script>";

	        /* script tag can be cached aggressively – it never changes unless botId changes */
	        return ResponseEntity.ok()
	                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
	                .body(tag);
	    }
	}