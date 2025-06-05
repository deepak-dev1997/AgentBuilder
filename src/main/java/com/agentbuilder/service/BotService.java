package com.agentbuilder.service;

import com.agentbuilder.dtos.BotDto;
import com.agentbuilder.dtos.BotUiConfig;
import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.KnowledgeBase;
import com.agentbuilder.model.ToolConfig;
import com.agentbuilder.repository.BotRepository;

import org.apache.commons.math3.random.ISAACRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BotService {

    @Autowired
    private BotRepository repository;


    public List<BotConfig> findAll() {
        return repository.findAll();
    }

    public BotConfig findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BotConfig not found with id " + id));
    }

    public BotConfig create(BotDto config) {
        BotConfig botConfig = new BotConfig();
        botConfig.setBotDescription(config.getBotDescription());
        botConfig.setBotName(config.getBotName());
        botConfig.setTools(new ArrayList<ToolConfig>());
        botConfig.setKnowledgeBase(new KnowledgeBase());
        botConfig.setTextColor("#ffffff");
        botConfig.setTextFont("");
        botConfig.setThemeColor("#7866ff");
        botConfig.setWelcomeMsg("Hello! I'm your assistant. How can I help you today?");
        
        return repository.save(botConfig);
    }

    public BotConfig update(String id, BotDto config) {
        BotConfig existing = findById(id);
        existing.setBotName(config.getBotName());
        existing.setBotDescription(config.getBotDescription());
  
        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
    
    public BotConfig updateThemeConfig(BotUiConfig botUiConfig,String botName) throws Exception {
    	Optional<BotConfig> botConfigOpt = repository.findById(botName);
    	if(botConfigOpt.isEmpty()) throw new Exception("Invalid bot id");
    	BotConfig botConfig = botConfigOpt.get();
    	botConfig.setThemeColor(botUiConfig.getThemeColor());
    	botConfig.setTextColor(botUiConfig.getTextColor());
    	botConfig.setTextFont(botUiConfig.getTextFont());
    	botConfig.setWelcomeMsg(botUiConfig.getWelcomeMessage());
    	botConfig = repository.save(botConfig);
    	return botConfig;
    	
    	
    	
    }
    
    public BotUiConfig getBotUiConfig(String botName) throws Exception{
    	Optional<BotConfig> botConfigOpt = repository.findById(botName);
    	if(botConfigOpt.isEmpty()) throw new Exception("Invalid bot id");
    	BotConfig botConfig = botConfigOpt.get();
    	BotUiConfig botUiConfig = new BotUiConfig();
    	botUiConfig.setAvatarUrl(botConfig.getAvatarUrl());
    	botUiConfig.setTextColor(botConfig.getTextColor());
    	botUiConfig.setTextFont(botConfig.getTextFont());
    	botUiConfig.setThemeColor(botConfig.getThemeColor());
    	botUiConfig.setWelcomeMessage(botConfig.getWelcomeMsg());
    	return botUiConfig;
    	}
    
}
