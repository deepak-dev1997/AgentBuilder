package com.agentbuilder.service;

import com.agentbuilder.exception.ResourceNotFoundException;
import com.agentbuilder.model.LLMConfig;
import com.agentbuilder.repository.LLMConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LLMConfigService {
    private final LLMConfigRepository repository;

    @Autowired
    public LLMConfigService(LLMConfigRepository repository) {
        this.repository = repository;
    }

    public List<LLMConfig> findAll() {
        return repository.findAll();
    }

    public LLMConfig findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LLMConfig not found with id " + id));
    }

    public LLMConfig create(LLMConfig config) {
        return repository.save(config);
    }

    public LLMConfig update(String id, LLMConfig config) {
        LLMConfig existing = findById(id);
        existing.setModel(config.getModel());
        existing.setTemperature(config.getTemperature());
        existing.setMaxTokens(config.getMaxTokens());
        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
