package com.agentbuilder.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agentbuilder.dtos.BotDto;
import com.agentbuilder.dtos.BotUiConfig;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.repository.BotRepository;
import com.agentbuilder.service.BotService;

@RestController
@RequestMapping("/api/bots")
@CrossOrigin(origins = "*")
public class BotController {

    @Autowired
    private BotService service;
    
    @Autowired
    private BotRepository botRepository;

    @GetMapping
    public List<BotConfig> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BotConfig getById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BotConfig create(@RequestBody BotDto config) {
        return service.create(config);
    }

    @PutMapping("/{id}")
    public BotConfig update(@PathVariable String id, @RequestBody BotDto config) {
        return service.update(id, config);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
    
    @PostMapping("/bots/{botId}/avatar")
    public String uploadAvatar(@PathVariable String botId,
                               @RequestParam MultipartFile file) throws IOException {

        String filename = UUID.randomUUID()+ "-" + file.getOriginalFilename();
        Path target = Paths.get("src/main/resources/static/uploads", filename);
        Files.copy(file.getInputStream(), target);

        BotConfig cfg = botRepository.findById(botId).orElseThrow();
        cfg.setAvatarUrl("/uploads/" + filename);
        botRepository.save(cfg);

        return cfg.getAvatarUrl();   
    }
    
    @PostMapping("updateTheme/{botName}")
    public BotConfig updateTheme(@PathVariable String botName, @RequestBody BotUiConfig botUiConfig) throws Exception {
    	return service.updateThemeConfig(botUiConfig, botName);
    }
    
    @GetMapping("/botUiConfig/{botName}")
    public BotUiConfig getBotUiConfig(@PathVariable String botName) throws Exception {
    	return service.getBotUiConfig(botName);
    }
    
}

