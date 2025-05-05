package com.agentbuilder.service;

import com.agentbuilder.dtos.BotDto;
import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.model.BotConfig;
import com.agentbuilder.model.KnowledgeBase;
import com.agentbuilder.model.ToolConfig;
import com.agentbuilder.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
